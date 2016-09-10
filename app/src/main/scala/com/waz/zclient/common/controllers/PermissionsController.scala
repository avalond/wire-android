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
package com.waz.zclient.common.controllers

import _root_.com.waz.threading.Threading
import _root_.com.waz.utils.events.{EventContext, EventStream}
import android.Manifest.permission
import android.app.Activity
import android.content.pm.PackageManager
import android.content.{Context, DialogInterface, Intent}
import android.net.Uri
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import com.waz.zclient.utils.ViewUtils
import com.waz.zclient.{ActivityHelper, Injectable, Injector, R}

import scala.concurrent.{Future, Promise}

class PermissionsController(sysPerms: PermissionsWrapper)(implicit injector: Injector) extends Injectable {

  import PermissionsController._

  private val activity = inject[PermissionActivity]

  implicit val context: Context = activity
  implicit val eventContext: EventContext = activity

  private var currentRequests = Map[Int, Promise[Set[Permission]]]()

  private def request(permissions: Set[Permission]) = {
    if (permissions.forall(_.hasPermission(sysPerms))) Future.successful(permissions)
    else {
      val requestCode = currentRequests.keys.lastOption.fold(startingRequestCode)(_ + 1)
      val promise = Promise[Set[Permission]]
      currentRequests = currentRequests + (requestCode -> promise)
      sysPerms.requestPermissions(activity, permissions, requestCode)
      promise.future
    }
  }

  def requiring(permissions: Set[Permission])(onGranted: => Unit)(dialogTitleId: Int = -1, dialogMessageId: Int = -1, onDenied: => Unit = ()) =
    request(permissions).map { ps => if (ps forall (_.hasPermission(sysPerms))) onGranted else {
      ViewUtils.showAlertDialog(
        activity,
        dialogTitleId,
        dialogMessageId,
        R.string.permissions_denied_dialog_acknowledge,
        R.string.permissions_denied_dialog_settings,
        new DialogInterface.OnClickListener() {
          override def onClick(dialog: DialogInterface, which: Int) = onDenied
        },
        new DialogInterface.OnClickListener() {
          override def onClick(dialog: DialogInterface, which: Int) = {
            val intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
          }
        });
    }}(Threading.Ui)

  activity.onPermissionResult.on(Threading.Ui) {
    case (requestCode, permissionNames, grantResults) =>
      val permissions = permissionNames.map(Permission(_)).toSet
      currentRequests.get(requestCode).foreach { p =>
        p.success(permissions)
        currentRequests -= requestCode
      }
  }
}

object PermissionsController {
  val startingRequestCode = 500
}

//Wrapper for removing dependency on static call to Android system
class PermissionsWrapper {
  def requestPermissions(activity: Activity, permissions: Set[Permission], requestId: Int): Unit = {
    ActivityCompat.requestPermissions(activity, permissions.map(_.name).toArray, requestId)
  }

  def checkSelfPermission(context: Context, name: String): Boolean = {
    PermissionChecker.checkSelfPermission(context, name) == PackageManager.PERMISSION_GRANTED
  }
}

sealed trait Permission {

  val name: String

  def hasPermission(permissionsWrapper: PermissionsWrapper)(implicit cxt: Context) = {
    permissionsWrapper.checkSelfPermission(cxt, name)
  }
}

object Permission {
  def apply(name: String): Permission = name match {
    case permission.RECORD_AUDIO => RecordAudioPermission
    case permission.CAMERA => CameraPermission
    case _ @ other => UnknownPermission(other)
  }
}

case object RecordAudioPermission extends Permission {
  override val name: String = permission.RECORD_AUDIO
}

case object CameraPermission extends Permission {
  override val name: String = permission.CAMERA
}

case class UnknownPermission(override val name: String) extends Permission

trait PermissionActivity extends ActivityHelper with ActivityCompat.OnRequestPermissionsResultCallback {

  protected[zclient] val onPermissionResult = EventStream[(Int, Array[String], Array[Int])]

  override def onRequestPermissionsResult(requestCode: Int, permissions: Array[String], grantResults: Array[Int]): Unit = {
    onPermissionResult ! (requestCode, permissions, grantResults)
  }
}

