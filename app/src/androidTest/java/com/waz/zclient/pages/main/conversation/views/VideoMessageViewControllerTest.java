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

import com.waz.api.Asset;
import com.waz.api.AssetStatus;
import com.waz.api.Message;
import com.waz.api.ProgressIndicator;
import com.waz.api.User;
import com.waz.zclient.MainTestActivity;
import com.waz.zclient.R;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.message.MessageViewControllerFactory;
import com.waz.zclient.pages.main.conversation.views.row.message.views.VideoMessageViewController;
import com.waz.zclient.pages.main.conversation.views.row.separator.Separator;
import com.waz.zclient.testutils.ViewTest;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.views.GlyphProgressView;
import org.junit.Test;
import org.threeten.bp.Duration;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.waz.zclient.testutils.CustomViewAssertions.isGone;
import static com.waz.zclient.testutils.CustomViewAssertions.isVisible;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VideoMessageViewControllerTest extends ViewTest<MainTestActivity> {

    public VideoMessageViewControllerTest() {
        super(MainTestActivity.class);
    }

    @Test
    public void verifyCancelFileUpload() throws InterruptedException {
        Message message = createMockMessage(Message.Status.PENDING);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS);
        ProgressIndicator progressIndicator = mock(ProgressIndicator.class);
        when(progressIndicator.getState()).thenReturn(ProgressIndicator.State.RUNNING);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, progressIndicator, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof VideoMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.gpv__row_conversation__video_button)).check(isVisible());
        onView(withText(activity.getString(R.string.glyph__close))).perform(click());

        verify(progressIndicator).cancel();
    }

    @Test
    public void verifyRetryUpload() {
        Message message = createMockMessage(Message.Status.FAILED);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_FAILED);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(
                                                                                       activity));
        assertTrue(viewController instanceof VideoMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.gpv__row_conversation__video_button)).check(isVisible());
        onView(withText(activity.getString(R.string.glyph__redo))).check(isVisible());
        onView(withId(R.id.gpv__row_conversation__video_button)).perform(click());

        verify(message).retry();
    }

    @Test
    public void verifyProgressIndicatorVisibleWhileDownloading() throws InterruptedException {
        Message message = createMockMessage(Message.Status.SENT);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.DOWNLOAD_IN_PROGRESS);
        ProgressIndicator progressIndicator = mock(ProgressIndicator.class);
        when(progressIndicator.isIndefinite()).thenReturn(true);
        when(progressIndicator.getState()).thenReturn(ProgressIndicator.State.RUNNING);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, progressIndicator, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof VideoMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.gpv__row_conversation__video_button)).check(isVisible());
        GlyphProgressView glyphProgressView = ViewUtils.getView(activity, R.id.gpv__row_conversation__video_button);
        assertTrue("Progress view is not animating", glyphProgressView.isAnimatingEndlessProgress());
    }

    @Test
    public void verifyProgressIndicatorVisibleForSenderWhileUploading() {
        Message message = createMockMessage(Message.Status.PENDING);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS);
        ProgressIndicator progressIndicator = mock(ProgressIndicator.class);
        when(progressIndicator.isIndefinite()).thenReturn(true);
        when(progressIndicator.getState()).thenReturn(ProgressIndicator.State.RUNNING);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, progressIndicator, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof VideoMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.gpv__row_conversation__video_button)).check(isVisible());
        GlyphProgressView glyphProgressView = ViewUtils.getView(activity, R.id.gpv__row_conversation__video_button);
        assertTrue("Progress view is not animating", glyphProgressView.isAnimatingEndlessProgress());
    }

    @Test
    public void verifyProgressIndicatorVisibleForReceiverWhileUploading() {
        Message message = createMockMessage(Message.Status.SENT);
        User user = createMockUser();
        Asset asset = createMockAsset(AssetStatus.UPLOAD_IN_PROGRESS);
        ProgressIndicator progressIndicator = mock(ProgressIndicator.class);
        when(progressIndicator.isIndefinite()).thenReturn(true);
        when(progressIndicator.getState()).thenReturn(ProgressIndicator.State.RUNNING);
        ViewControllerMockHelper.setupMessageMock(message, user, asset, progressIndicator, activity);

        MessageViewController viewController = MessageViewControllerFactory.create(activity,
                                                                                   message,
                                                                                   ViewControllerMockHelper.getMockMessageViewsContainer(activity));
        assertTrue(viewController instanceof VideoMessageViewController);

        viewController.setMessage(message, mock(Separator.class));

        setView(viewController.getView());

        onView(withId(R.id.gpv__row_conversation__video_button)).check(isGone());
    }

    private Asset createMockAsset(AssetStatus status) {
        Asset asset = mock(Asset.class);
        when(asset.getStatus()).thenReturn(status);
        when(asset.getName()).thenReturn("audio_recording.mp4");
        when(asset.getSizeInBytes()).thenReturn(10256L);
        when(asset.getMimeType()).thenReturn("application/vnd.android.package-archive");
        when(asset.getDuration()).thenReturn(Duration.ZERO);
        return asset;
    }

    private User createMockUser() {
        User user = mock(User.class);
        when(user.getDisplayName()).thenReturn("Test User");
        when(user.getId()).thenReturn("someRandomString");
        return user;
    }

    private Message createMockMessage(Message.Status status) {
        Message message = mock(Message.class);
        when(message.getMessageType()).thenReturn(Message.Type.VIDEO_ASSET);
        when(message.getMessageStatus()).thenReturn(status);
        return message;
    }

}
