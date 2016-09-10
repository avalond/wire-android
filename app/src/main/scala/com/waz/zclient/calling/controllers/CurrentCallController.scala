/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.calling.controllers

import _root_.com.waz.zclient.utils.LayoutSpec
import android.media.AudioManager
import android.os.Vibrator
import com.waz.api.VideoSendState.{DONT_SEND, SEND}
import com.waz.api.VoiceChannelState._
import com.waz.api._
import com.waz.avs.{VideoPreview, VideoRenderer}
import com.waz.model.VoiceChannelData.ConnectionState
import com.waz.model._
import com.waz.service.call.FlowManagerService.{StateAndReason, UnknownState}
import com.waz.threading.Threading
import com.waz.utils._
import com.waz.utils.events.{ClockSignal, Signal}
import com.waz.zclient._
import org.threeten.bp.Duration
import org.threeten.bp.Duration._
import org.threeten.bp.Instant._
import timber.log.Timber


class CurrentCallController(implicit inj: Injector, cxt: WireContext) extends Injectable { self =>

  Timber.d(s"CurrentCallController starting in context: $cxt")

  val globController = inject[GlobalCallingController]

  implicit val eventContext = cxt.eventContext

  val zms = globController.zms.collect { case Some(zms) => zms }

  val wasUiActiveOnCallStart = globController.wasUiActiveOnCallStart

  val videoCall = globController.videoCall

  val convId = globController.convId.collect { case Some(convId) => convId }

  val currentChannel = globController.currentChannel.collect { case Some(ch) => ch }

  val voiceService = globController.voiceService

  val voiceServiceAndCurrentConvId = globController.voiceServiceAndCurrentConvId.collect { case Some(vcAndConv) => vcAndConv }

  val callState = globController.callState.collect { case Some(state) => state }

  val activeCall = globController.activeCall

  val showOngoingControls = callState.flatMap {
    case OTHER_CALLING | OTHERS_CONNECTED | TRANSFER_CALLING | TRANSFER_READY => Signal(false)
    case SELF_CALLING | SELF_JOINING | SELF_CONNECTED => Signal(true)
    case _ => Signal.empty[Boolean]
  }

  val selfUser = zms flatMap (_.users.selfUser)

  val userStorage = zms map (_.usersStorage)

  val deviceState = currentChannel map (_.deviceState)

  val conversation = zms.zip(convId) flatMap { case (zms, convId) => zms.convsStorage.signal(convId) }

  val conversationName = conversation map (data => if (data.convType == IConversation.Type.GROUP) data.name.filter(!_.isEmpty).getOrElse(data.generatedName) else data.generatedName)

  val groupCall = currentChannel map (_.tracking.kindOfCall == KindOfCall.GROUP)

  val silenced = currentChannel map (_.silenced)

  val muted = currentChannel map (_.muted)

  val videoSendState = currentChannel map (_.video.videoSendState)

  val captureDevices = currentChannel map (_.video.captureDevices)

  //TODO when I have a proper field for front camera, make sure it's always set as the first one
  val currentCaptureDeviceIndex = Signal(0)

  val currentCaptureDevice = captureDevices.zip(currentCaptureDeviceIndex).map {
    case (devices, devIndex) if devices.size > 0 => Some(devices(devIndex % devices.size))
    case _ => None
  }

  voiceServiceAndCurrentConvId.zip(currentCaptureDevice) {
    case ((vcs, convId), Some(dev)) => vcs.setVideoCaptureDevice(convId, dev.id)
    case _ =>
  }

  val otherUser = Signal(groupCall, userStorage, convId).flatMap {
    case (isGroupCall, usersStorage, convId) if isGroupCall == false =>
      usersStorage.optSignal(UserId(convId.str)) // one-to-one conversation has the same id as the other user, so we can access it directly
    case _ => Signal.const[Option[UserData]](None) //Need a none signal to help with further signals
  }

  val callEstablished = currentChannel map (_.deviceState == ConnectionState.Connected)

  val onCallEstablished = callEstablished.onChanged

  val outgoingCall = currentChannel map (_.state == SELF_CALLING)

  val callerId = currentChannel map (_.caller) flatMap (_.fold(Signal.empty[UserId])(Signal(_)))

  val callerData = userStorage.zip(callerId).flatMap { case (storage, id) => storage.signal(id) }

  val duration = currentChannel flatMap {
    case ch if ch.deviceState == ConnectionState.Connected => new ClockSignal(Duration.ofSeconds(1).asScala).map(_ => ch.tracking.established.fold2(ZERO, between(_, now)))
    case _ => Signal.const(ZERO)
  } map { duration =>
    val seconds = ((duration.toMillis / 1000) % 60).toInt
    val minutes = ((duration.toMillis / 1000) / 60).toInt
    f"$minutes%02d:$seconds%02d"
  }

  val subtitleText = Signal(outgoingCall, videoCall, callState, duration) map {
    case (true, true, SELF_CALLING, _) => cxt.getString(R.string.calling__header__outgoing_video_subtitle)
    case (true, false, SELF_CALLING, _) => cxt.getString(R.string.calling__header__outgoing_subtitle)
    case (false, true, OTHER_CALLING, _) => cxt.getString(R.string.calling__header__incoming_subtitle__video)
    case (false, false, OTHER_CALLING | OTHERS_CONNECTED, _) => cxt.getString(R.string.calling__header__incoming_subtitle)
    case (_, _, SELF_JOINING, _) => cxt.getString(R.string.calling__header__joining)
    case (false, false, SELF_CONNECTED, duration) => duration
    case _ => ""
  }

  val otherParticipants = currentChannel.zip(selfUser) map {
    case (ch, selfUser) if (ch.state == SELF_CONNECTED) => for (p <- ch.participants if selfUser.id != p.userId && p.state == ConnectionState.Connected) yield p
    case _ => Vector.empty
  }

  val otherSendingVideo = otherParticipants map {
    case Vector(other) => other.sendsVideo
    case _ => false
  }

  val flowManager = zms map (_.flowmanager)

  val avsStateAndChangeReason = flowManager.flatMap(_.stateOfReceivedVideo)
  val cameraFailed = flowManager.flatMap(_.cameraFailedSig)

  val stateMessageText = Signal(callState, cameraFailed, avsStateAndChangeReason, conversationName, otherSendingVideo) map { values =>
    Timber.d(s"(callState, avsStateAndChangeReason, conversationName, otherSending): $values")
    values match {
      case (SELF_CALLING, true, _, _, _) => Option(cxt.getString(R.string.calling__self_preview_unavailable_long))
      case (SELF_JOINING, _, _, _, _) => Option(cxt.getString(R.string.ongoing__connecting))
      case (SELF_CONNECTED, _, StateAndReason(AvsVideoState.STOPPED, AvsVideoReason.BAD_CONNECTION), _, true) => Option(cxt.getString(R.string.ongoing__poor_connection_message))
      case (SELF_CONNECTED, _, _, otherUserName, false) => Option(cxt.getString(R.string.ongoing__other_turned_off_video, otherUserName))
      case (SELF_CONNECTED, _, UnknownState, otherUserName, true) => Option(cxt.getString(R.string.ongoing__other_unable_to_send_video, otherUserName))
      case _ => None
    }
  }

  val participantIdsToDisplay = Signal(otherParticipants, groupCall, callerData, otherUser).map { values =>
    Timber.d(s"(otherParticipants, groupCall, callerData, otherUser): $values")
    values match {
      case (parts, true, callerData, _) if parts.isEmpty => Vector(callerData.id)
      case (parts, false, _, Some(otherUser)) if parts.isEmpty => Vector(otherUser.id)
      case (parts, _, _, _) => parts.map(_.userId)
      case _ => Vector.empty[UserId]
    }
  }

  val flowId = for {
    zms <- zms
    convId <- convId
    conv <- zms.convsStorage.signal(convId)
    rConvId = conv.remoteId
    userData <- otherUser
  } yield (rConvId, userData.map(_.id))

  def setVideoPreview(view: Option[VideoPreview]): Unit = {
    flowManager.on(Threading.Ui) { fm =>
      Timber.d(s"Setting VideoPreview on Flowmanager, view: $view")
      fm.setVideoPreview(view.getOrElse(null))
    }
  }

  def setVideoView(view: Option[VideoRenderer]): Unit = {
    (for {
      fm <- flowManager
      (rConvId, userId) <- flowId
    } yield (fm, rConvId, userId)).on(Threading.Ui) {
      case (fm, rConvId, userId) =>
        Timber.d(s"Setting ViewRenderer on Flowmanager, rConvId: $rConvId, userId: $userId, view: $view")
        view.foreach(fm.setVideoView(rConvId, userId, _))
    }
  }

  //Set the following signals to keep track of updates as the following methods rely on their values
  voiceServiceAndCurrentConvId.disableAutowiring()
  videoCall.disableAutowiring()
  muted.disableAutowiring()
  videoSendState.disableAutowiring()
  callEstablished.disableAutowiring()

  def dismissCall(): Unit = {
    voiceServiceAndCurrentConvId.currentValue.foreach {
      case (vcs, id) => vcs.silenceVoiceChannel(id)
    }
  }

  def leaveCall(): Unit = {
    voiceServiceAndCurrentConvId.currentValue.foreach {
      case (vcs, id) => vcs.leaveVoiceChannel(id)
    }
  }

  def vibrate(): Unit = {
    val audioManager = Option(inject[AudioManager])
    val vibrator = Option(inject[Vibrator])

    val disableRepeat = -1
    (audioManager, vibrator) match {
      case (Some(am), Some(vib)) if am.getRingerMode != AudioManager.RINGER_MODE_SILENT =>
        vib.vibrate(cxt.getResources.getIntArray(R.array.call_control_enter).map(_.toLong), disableRepeat)
      case _ =>
    }
  }

  def toggleMuted(): Unit = {
    voiceServiceAndCurrentConvId.currentValue.foreach {
      case (vcs, id) => if (muted.currentValue.getOrElse(false)) vcs.unmuteVoiceChannel(id) else vcs.muteVoiceChannel(id)
    }
  }

  def toggleVideo(): Unit = {
    voiceServiceAndCurrentConvId.currentValue.foreach {
      case (vcs, id) => vcs.setVideoSendState(id, if (videoSendState.currentValue.getOrElse(DONT_SEND) == SEND) DONT_SEND else SEND)
    }
  }

  val speakerEnabled = Signal(false)

  val isTablet = Signal(LayoutSpec.isTablet(cxt))

  zms.zip(speakerEnabled) {
    case (zms, setSpeaker) => zms.mediamanager.setSpeaker(setSpeaker)
  }

  val rightButtonShown = Signal(videoCall, callEstablished, captureDevices, isTablet) map {
    case (true, false, _, _) => false
    case (true, true, captureDevices, _) => captureDevices.size >= 0
    case (false, _, _, isTablet) => !isTablet //Tablets don't have ear-pieces, so you can't switch between speakers
    case _ => false
  }
}
