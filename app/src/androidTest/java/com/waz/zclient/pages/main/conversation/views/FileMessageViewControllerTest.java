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
package com.waz.zclient.pages.main.conversation.views;

import android.webkit.MimeTypeMap;
import com.waz.api.Asset;
import com.waz.api.AssetStatus;
import com.waz.api.Message;
import com.waz.api.User;
import com.waz.api.impl.ProgressIndicator;
import com.waz.zclient.MainTestActivity;
import com.waz.zclient.R;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewControllerFactory;
import com.waz.zclient.pages.main.conversation.views.row.message.views.FileMessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.testutils.ViewTest;
import org.junit.Test;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.waz.api.ProgressIndicator.State;
import static com.waz.zclient.testutils.CustomViewAssertions.hasText;
import static com.waz.zclient.testutils.CustomViewAssertions.isVisible;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FileMessageViewControllerTest extends ViewTest<MainTestActivity> {

    public FileMessageViewControllerTest() {
        super(MainTestActivity.class);
    }

    @Test
    public void verifyCancelFileUpload() throws InterruptedException {
        Message message = createMockMessage(Message.Status.PENDING);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS, State.RUNNING);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.aab__row_conversation__action_button)).check(isVisible());
        onView(withText(activity.getString(R.string.glyph__close))).check(isVisible());
        onView(withId(R.id.aab__row_conversation__action_button)).perform(click());
        Thread.sleep(400);

        verify(asset.getUploadProgress()).cancel();
    }

    @Test
    public void verifyRetryUpload() throws InterruptedException {
        Message message = createMockMessage(Message.Status.FAILED);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(
                                                                                       activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.aab__row_conversation__action_button)).check(isVisible());
        onView(withText(activity.getString(R.string.glyph__redo))).check(isVisible());
        onView(withId(R.id.aab__row_conversation__action_button)).perform(click());
        Thread.sleep(400);

        verify(message).retry();
    }

    @Test
    public void verifyProgressIndicatorVisibleWhileDownloading() {
        Message message = createMockMessage(Message.Status.SENT);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.DOWNLOAD_IN_PROGRESS);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.aab__row_conversation__action_button)).check(isVisible());
        onView(withText(activity.getString(R.string.glyph__close))).check(isVisible()); //best way to check if progress bar is displayed is to check the file placeholder isn't yet shown
    }

    @Test
    public void verifyProgressIndicatorVisibleForSenderWhileUploading() {
        Message message = createMockMessage(Message.Status.PENDING);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.aab__row_conversation__action_button)).check(isVisible());
        onView(withText(activity.getString(R.string.glyph__close))).check(isVisible()); //best way to check if progress bar is displayed is to check the file placeholder isn't yet shown
    }

    @Test
    public void verifyProgressIndicatorVisibleForReceiverWhileUploading() {
        Message message = createMockMessage(Message.Status.SENT);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.pdv__row_conversation__file_placeholder_dots)).check(isVisible());
    }

    @Test
    public void verifyProgressIndicatorGoneForReceiverWhenDownloadDone() {
        Message message = createMockMessage(Message.Status.SENT);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.DOWNLOAD_DONE);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(
                                                                                       activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.aab__row_conversation__action_button)).check(isVisible());
        onView(withId(R.id.aab__row_conversation__action_button)).check(matches(withText(""))); //the `glyph_file` string is actually used as a background...
    }

    @Test
    public void verifyFileInfoIsCorrectWhenFileSizeIsNotAvailableDuringUpload() {
        Message message = createMockMessage(Message.Status.PENDING);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_NOT_STARTED);
        when(asset.getSizeInBytes()).thenReturn(-1L);
        when(asset.getMimeType()).thenReturn("application/pdf");
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String fileExtension = mimeTypeMap.getExtensionFromMimeType(asset.getMimeType());
        String label = activity.getString(R.string.content__file__status__uploading, fileExtension.toUpperCase(Locale.getDefault()));

        onView(withId(R.id.ttv__row_conversation__file__fileinfo)).check(hasText(label));
        onView(withId(R.id.ttv__row_conversation__file__fileinfo)).check(isVisible());
    }

    @Test
    public void verifyFileInfoIsCorrectWhenFileSizeAndExtensionAreNotAvailableDuringUpload() {
        Message message = createMockMessage(Message.Status.PENDING);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_NOT_STARTED);
        when(asset.getSizeInBytes()).thenReturn(-1L);
        when(asset.getMimeType()).thenReturn("");
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof FileMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        String label = activity.getString(R.string.content__file__status__uploading__minimized);

        onView(withId(R.id.ttv__row_conversation__file__fileinfo)).check(hasText(label));
        onView(withId(R.id.ttv__row_conversation__file__fileinfo)).check(isVisible());
    }

    private Asset createMockAsset(AssetStatus status) {
        return createMockAsset(status, ProgressIndicator.State.CANCELLED);
    }

    private Asset createMockAsset(AssetStatus status, ProgressIndicator.State progressIndicatorState) {
        Asset asset = mock(Asset.class);
        ProgressIndicator progressIndicator = mock(ProgressIndicator.class);
        when(progressIndicator.getState()).thenReturn(progressIndicatorState);
        when(asset.getStatus()).thenReturn(status);
        when(asset.getName()).thenReturn("some_file.apk");
        when(asset.getSizeInBytes()).thenReturn(10256L);
        when(asset.getMimeType()).thenReturn("application/vnd.android.package-archive");
        when(asset.getUploadProgress()).thenReturn(progressIndicator);
        return asset;
    }

    private User createMockUser() {
        User user = mock(User.class);
        when(user.getDisplayName()).thenReturn("Test User");
        when(user.getId()).thenReturn("asdfasfdasdf");
        return user;
    }

    private Message createMockMessage(Message.Status status) {
        Message message = mock(Message.class);
        when(message.getMessageType()).thenReturn(Message.Type.ANY_ASSET);
        when(message.getMessageStatus()).thenReturn(status);
        return message;
    }
}
