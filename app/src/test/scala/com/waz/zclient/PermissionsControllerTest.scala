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
package com.waz.zclient

import java.util.concurrent.{TimeoutException, TimeUnit}

import _root_.com.waz.RobolectricUtils
import _root_.com.waz.testutils.TestWireContext
import android.app.Activity
import android.content.Context
import com.waz.zclient.common.controllers._
import junit.framework.Assert.{assertFalse, assertTrue}
import org.junit.runner.RunWith
import org.junit.{Before, Test}
import org.mockito.Matchers.{any, same}
import org.mockito.Mockito._
import org.robolectric.annotation.Config
import org.robolectric.{Robolectric, RobolectricTestRunner}
import org.scalatest.junit.JUnitSuite
import org.scalatest.{Informer, Informing}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@RunWith(classOf[RobolectricTestRunner])
@Config(manifest = Config.NONE)
class PermissionsControllerTest extends JUnitSuite with RobolectricUtils with Informing {

  implicit val timeout = Duration(1000, TimeUnit.MILLISECONDS)

  var activity: TestPermissionsActivity = _
  var controller: PermissionsController = _
  var mockPermissionsWrapper: PermissionsWrapper = _

  @Before
  def setup() = {
    implicit val context = mock(classOf[TestWireContext])
    activity = Robolectric.buildActivity(classOf[TestPermissionsActivity]).create().start().resume().visible().get()

    implicit val module = new Module {
      bind[PermissionActivity] to activity
    }

    mockPermissionsWrapper = mock(classOf[PermissionsWrapper])
    controller = new PermissionsController(mockPermissionsWrapper)
  }

  @Test
  def alreadyGrantedPermissionShouldPerformOnGranted(): Unit = {
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(true)
    var permGranted = false
    Await.ready(controller.requiring(Set(CameraPermission))((permGranted = true))(-1, -1, (permGranted = false)), Duration(1000, TimeUnit.MILLISECONDS))
    assertTrue(permGranted)
  }

  @Test
  def notGrantedPermissionShouldRequestPermission(): Unit = {
    val permissions = Set[Permission](CameraPermission)
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(false)
    controller.requiring(permissions) {} (-1, -1, ())
    verify(mockPermissionsWrapper).requestPermissions(activity, permissions, PermissionsController.startingRequestCode)
  }

  @Test
  def requestMultiplePermissionsAtOnce(): Unit = {
    val permissions = Set[Permission](CameraPermission, RecordAudioPermission)
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(false)
    controller.requiring(permissions) {} (-1, -1, ())
    verify(mockPermissionsWrapper).requestPermissions(activity, permissions, PermissionsController.startingRequestCode)
  }

  @Test
  def onPermissionGrantedByUserPerformOnGranted(): Unit = {
    val permissions = Set[Permission](CameraPermission)
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(false)
    var permGranted = false
    val future = controller.requiring(permissions)((permGranted = true))(-1, -1, (permGranted = false))
    //user grants permission
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(true)
    activity.onRequestPermissionsResult(PermissionsController.startingRequestCode, permissions.map(_.name).toArray, Array(1))

    awaitUiFuture(future)

    assertTrue(permGranted)
  }

  @Test
  def multiplePermissionRequestsInSeriesRemainDistinctRequests(): Unit = {
    val permissions1 = Set[Permission](CameraPermission)
    val permissions2 = Set[Permission](RecordAudioPermission)

    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), any(classOf[String]))).thenReturn(false)
    var perm1Granted = false
    val future1 = controller.requiring(permissions1)((perm1Granted = true))(-1, -1, (perm1Granted = false))

    var perm2Granted = false
    val future2 = controller.requiring(permissions2)((perm2Granted = true))(-1, -1, (perm2Granted = false))

    //user grants permission1
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(true)
    activity.onRequestPermissionsResult(PermissionsController.startingRequestCode, permissions1.map(_.name).toArray, Array(1))

    awaitUiFuture(future1)

    assertTrue(perm1Granted)
    assertFalse(perm2Granted)

    //user grants permission2
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(RecordAudioPermission.name))).thenReturn(true)
    activity.onRequestPermissionsResult(PermissionsController.startingRequestCode + 1, permissions2.map(_.name).toArray, Array(1))

    awaitUiFuture(future2)

    assertTrue(perm1Granted)
    assertTrue(perm2Granted)
  }

  @Test(expected = classOf[TimeoutException])
  def incomingResultFromAnotherRequesterDoesNotInterfere(): Unit = {
    val permissions = Set[Permission](CameraPermission)
    when(mockPermissionsWrapper.checkSelfPermission(any(classOf[Context]), same(CameraPermission.name))).thenReturn(false)
    var permGranted = false
    val future = controller.requiring(permissions)((permGranted = true))(-1, -1, (permGranted = false))

    //Some other request is granted from a different place in the app
    activity.onRequestPermissionsResult(1945, Array(RecordAudioPermission.name), Array(1))

    //give some time to allow the EventStream to fire
    awaitUiFuture(future)(Duration(200, TimeUnit.MILLISECONDS))

    //assert that nothing has happened
    assertFalse(permGranted)
  }

  override protected def info: Informer = new Informer {
    override def apply(message: String, payload: Option[Any]): Unit = println(message)
  }
}

class TestPermissionsActivity extends Activity with PermissionActivity
