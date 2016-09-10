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
package com.waz.zclient.pages.main.giphy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.waz.api.GiphyResults;
import com.waz.api.IConversation;
import com.waz.api.ImageAsset;
import com.waz.api.LoadHandle;
import com.waz.api.UpdateListener;
import com.waz.zclient.OnBackPressedListener;
import com.waz.zclient.R;
import com.waz.zclient.controllers.accentcolor.AccentColorObserver;
import com.waz.zclient.core.controllers.tracking.events.media.OpenedGiphyGridEvent;
import com.waz.zclient.core.stores.network.NetworkStoreObserver;
import com.waz.zclient.pages.BaseFragment;
import com.waz.zclient.pages.main.profile.views.ConfirmationMenu;
import com.waz.zclient.pages.main.profile.views.ConfirmationMenuListener;
import com.waz.zclient.ui.utils.KeyboardUtils;
import com.waz.zclient.ui.utils.TextViewUtils;
import com.waz.zclient.utils.TrackingUtils;
import com.waz.zclient.utils.ViewUtils;
import com.waz.zclient.views.LoadingIndicatorView;
import com.waz.zclient.views.images.ImageAssetView;

public class GiphySharingPreviewFragment extends BaseFragment<GiphySharingPreviewFragment.Container> implements AccentColorObserver,
                                                                                                                ImageAssetView.BitmapLoadedCallback,
                                                                                                                NetworkStoreObserver,
                                                                                                                GiphyGridViewAdapter.ScrollGifCallback,
                                                                                                                OnBackPressedListener {

    public static final String TAG = GiphySharingPreviewFragment.class.getSimpleName();
    public static final String ARG_SEARCH_TERM = "SEARCH_TERM";
    private String searchTerm;
    private ImageAssetView previewImageAssetView;
    private TextView giphyTitle;
    private TextView conversationTitle;
    private ConfirmationMenu confirmationMenu;
    private View gifBackButton;
    private View gifLinkButton;
    private LoadHandle searchHandle;
    private ImageAsset foundImage;
    private LoadingIndicatorView loadingIndicator;
    private TextView errorView;
    private RecyclerView recyclerView;
    private GiphyGridViewAdapter giphyGridViewAdapter;

    private GiphyResults giphyResults;
    private int currentImageAssetIndex = 0;
    private int currentGifsLoaded = 0;

    private final UpdateListener giphyResultUpdateListener = new UpdateListener() {
        @Override
        public void updated() {
            if (giphyGridViewAdapter == null) {
                return;
            }
            giphyGridViewAdapter.notifyItemRangeInserted(currentGifsLoaded, giphyResults.size() - currentGifsLoaded);
            currentGifsLoaded = giphyResults.size();
            showNextImage();
        }
    };

    private final UpdateListener conversationListUpdateListener = new UpdateListener() {
        @Override
        public void updated() {
            if (getActivity() == null ||
                getStoreFactory().isTornDown()) {
                return;
            }
            if (getStoreFactory().getConversationStore().getCurrentConversation() != null) {
                conversationTitle.setText(getStoreFactory().getConversationStore().getCurrentConversation().getName());
            }
        }
    };

    private ConfirmationMenuListener confirmationMenuListener = new ConfirmationMenuListener() {
        @Override
        public void confirm() {
            sendGif();
        }

        @Override
        public void cancel() {
            showNextImage();
        }
    };

    @Override
    public void onConnectivityChange(boolean hasInternet, boolean isServerError) {
        if (!hasInternet) {
            onLossOfNetworkConnection();
            return;
        }
        onResumedNetworkConnection();
    }

    @Override
    public void onNoInternetConnection(boolean isServerError) {

    }

    public static GiphySharingPreviewFragment newInstance() {
        GiphySharingPreviewFragment fragment = new GiphySharingPreviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static GiphySharingPreviewFragment newInstance(String searchTerm) {
        GiphySharingPreviewFragment fragment = new GiphySharingPreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_TERM, searchTerm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            args = savedInstanceState;
        }
        searchTerm = args.getString(ARG_SEARCH_TERM, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_giphy_preview, container, false);
        loadingIndicator = ViewUtils.getView(view, R.id.liv__giphy_preview__loading);
        loadingIndicator.setType(LoadingIndicatorView.INFINITE_LOADING_BAR);
        previewImageAssetView = ViewUtils.getView(view, R.id.iv__giphy_preview__preview);
        previewImageAssetView.setShowPreview(true);
        previewImageAssetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foundImage != null) {
                    ViewUtils.fadeOutView(previewImageAssetView);
                    ViewUtils.fadeOutView(confirmationMenu);
                    ViewUtils.fadeOutView(gifLinkButton);
                    ViewUtils.fadeInView(recyclerView);
                    ViewUtils.fadeInView(gifBackButton);

                    foundImage.removeUpdateListener(previewImageAssetView);
                    if (searchHandle != null) {
                        searchHandle.cancel();
                    }
                }
                previewImageAssetView.setImageAsset(null);
            }
        });

        errorView = ViewUtils.getView(view, R.id.ttv__giphy_preview__error);
        errorView.setVisibility(View.GONE);
        giphyTitle = ViewUtils.getView(view, R.id.ttv__giphy_preview__title);
        conversationTitle = ViewUtils.getView(view, R.id.ttv__giphy_conversation_name_title);

        giphyGridViewAdapter = new GiphyGridViewAdapter(getActivity());
        recyclerView = ViewUtils.getView(view, R.id.rv__giphy_image_preview);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(giphyGridViewAdapter);
        recyclerView.setVisibility(View.GONE);

        gifBackButton = ViewUtils.getView(view, R.id.gtv__giphy_previous_button);
        gifBackButton.setVisibility(View.GONE);
        final View closeButton = ViewUtils.getView(view, R.id.gtv__giphy_preview__close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getControllerFactory().getGiphyController().cancel();
            }
        });
        gifLinkButton = ViewUtils.getView(view, R.id.gtv__giphy_preview__link_button);
        gifLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGrid();
            }
        });
        gifBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeGrid();
            }
        });
        confirmationMenu = ViewUtils.getView(view, R.id.cm__giphy_preview__confirmation_menu);
        confirmationMenu.setConfirmationMenuListener(confirmationMenuListener);
        confirmationMenu.setConfirm(getString(R.string.sharing__image_preview__confirm_action));
        confirmationMenu.setCancel(getString(R.string.giphy_preview__try_another_action));
        confirmationMenu.setWireTheme(getControllerFactory().getThemeController().getThemeDependentOptionsTheme());
        return view;
    }

    private void showGrid() {
        previewImageAssetView.setImageBitmap(null);
        previewImageAssetView.setImageAsset(null);
        ViewUtils.fadeOutView(previewImageAssetView);
        ViewUtils.fadeOutView(confirmationMenu);
        ViewUtils.fadeOutView(gifLinkButton);
        ViewUtils.fadeInView(recyclerView);
        ViewUtils.fadeInView(gifBackButton);

        if (foundImage != null) {
            foundImage.removeUpdateListener(previewImageAssetView);
        }
        if (searchHandle != null) {
            searchHandle.cancel();
        }
        getControllerFactory().getTrackingController().tagEvent(new OpenedGiphyGridEvent());
    }

    private void closeGrid() {
        setSelectedGifFromGridView(foundImage);
        ViewUtils.fadeInView(previewImageAssetView);
        ViewUtils.fadeInView(confirmationMenu);
        ViewUtils.fadeInView(gifLinkButton);
        ViewUtils.fadeOutView(recyclerView);
        ViewUtils.fadeOutView(gifBackButton);
        if (foundImage != null) {
            foundImage.addUpdateListener(previewImageAssetView);
        }
    }

    private void sendGif() {
        TrackingUtils.onSentGifMessage(getControllerFactory().getTrackingController(),
                                       getStoreFactory().getConversationStore().getCurrentConversation());

        if (searchTerm == null) {
            getStoreFactory().getConversationStore().sendMessage(getString(R.string.giphy_preview__message_via_random));
        } else {
            getStoreFactory().getConversationStore().sendMessage(getString(R.string.giphy_preview__message_via_search,
                                                                           searchTerm));
        }
        getStoreFactory().getNetworkStore().doIfHasInternetOrNotifyUser(null);
        getStoreFactory().getConversationStore().sendMessage(foundImage);
        getControllerFactory().getGiphyController().close();
    }

    @Override
    public void onStart() {
        super.onStart();
        KeyboardUtils.hideKeyboard(getActivity());
        giphyGridViewAdapter.setScrollGifCallback(this);
        getControllerFactory().getAccentColorController().addAccentColorObserver(this);
        getStoreFactory().getInAppNotificationStore().setUserSendingPicture(true);
        getStoreFactory().getNetworkStore().addNetworkStoreObserver(this);
        IConversation conversation = getStoreFactory().getConversationStore().getCurrentConversation();
        if (conversation != null) {
            conversation.addUpdateListener(conversationListUpdateListener);
            conversationTitle.setText(conversation.getName());
        }
    }

    @Override
    public void onStop() {
        getStoreFactory().getInAppNotificationStore().setUserSendingPicture(false);
        getControllerFactory().getAccentColorController().removeAccentColorObserver(this);
        getStoreFactory().getNetworkStore().removeNetworkStoreObserver(this);
        IConversation conversation = getStoreFactory().getConversationStore().getCurrentConversation();
        if (conversation != null) {
            conversation.removeUpdateListener(conversationListUpdateListener);
        }
        giphyGridViewAdapter.setScrollGifCallback(null);
        if (giphyResults != null) {
            giphyResults.removeUpdateListener(giphyResultUpdateListener);
        }
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String gifTitle;
        if (searchTerm == null) {
            gifTitle = getString(R.string.giphy_preview__title_random);
        } else {
            gifTitle = getString(R.string.giphy_preview__title_search, searchTerm);
        }
        giphyTitle.setText(gifTitle);
        onSearch(searchTerm);
    }

    @Override
    public void onAccentColorHasChanged(Object sender, int color) {
        confirmationMenu.setAccentColor(color);
        if (!getControllerFactory().getThemeController().isDarkTheme()) {
            confirmationMenu.setCancelColor(color, color);
            confirmationMenu.setConfirmColor(getResources().getColor(R.color.white), color);
        }
        loadingIndicator.setColor(color);
    }

    @Override
    public void onDestroyView() {
        if (searchHandle != null) {
            searchHandle.cancel();
            searchHandle = null;
        }
        previewImageAssetView.clearImage();
        previewImageAssetView = null;
        giphyTitle = null;
        confirmationMenu = null;
        foundImage = null;
        recyclerView = null;
        giphyGridViewAdapter = null;
        gifLinkButton = null;
        gifBackButton = null;
        giphyResults = null;
        super.onDestroyView();
    }

    private void showNextImage() {
        if (giphyResults == null ||
            giphyResults.size() == 0) {
            showError();
            return;
        }
        foundImage = giphyResults.get(currentImageAssetIndex);
        previewImageAssetView.setBitmapLoadedCallback(this);
        previewImageAssetView.setImageAsset(foundImage);
        if (currentImageAssetIndex < giphyResults.size() - 1) {
            currentImageAssetIndex++;
        } else {
            currentImageAssetIndex = 0;
        }
    }

    private void onSearch(String keyword) {
        if (searchHandle != null) {
            searchHandle.cancel();
            searchHandle = null;
        }
        confirmationMenu.setConfirmEnabled(false);
        errorView.setVisibility(View.GONE);
        previewImageAssetView.clearImage();
        loadingIndicator.show();
        if (searchTerm == null) {
            giphyResults = getStoreFactory().getZMessagingApiStore()
                                            .getApi()
                                            .getGiphy()
                                            .random();

        } else {
            giphyResults = getStoreFactory().getZMessagingApiStore()
                                            .getApi()
                                            .getGiphy()
                                            .search(keyword);
        }
        giphyResults.whenReady(new Runnable() {
            @Override
            public void run() {
                giphyResultUpdateListener.updated();
            }
        });
        giphyResults.addUpdateListener(giphyResultUpdateListener);
        giphyGridViewAdapter.setGiphyResults(giphyResults);
    }

    private void showError() {
        loadingIndicator.hide();
        previewImageAssetView.clearImage();
        errorView.setText(R.string.giphy_preview__error);
        TextViewUtils.mediumText(errorView);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBitmapLoadFinished(boolean bitmapLoaded) {
        confirmationMenu.setConfirmEnabled(true);
        loadingIndicator.hide();
        previewImageAssetView.setBitmapLoadedCallback(null);
    }

    @Override
    public void setSelectedGifFromGridView(ImageAsset gifAsset) {
        foundImage = gifAsset;
        previewImageAssetView.setImageAsset(gifAsset);
        ViewUtils.fadeInView(previewImageAssetView);
        ViewUtils.fadeInView(confirmationMenu);
        ViewUtils.fadeInView(gifLinkButton);
        ViewUtils.fadeOutView(recyclerView);
        ViewUtils.fadeOutView(gifBackButton);
    }

    private void onLossOfNetworkConnection() {
        gifLinkButton.setClickable(false);
        confirmationMenu.setConfirmationMenuListener(null);
        previewImageAssetView.setClickable(false);
    }

    private void onResumedNetworkConnection() {
        gifLinkButton.setClickable(true);
        confirmationMenu.setConfirmationMenuListener(confirmationMenuListener);
        previewImageAssetView.setClickable(true);
    }

    @Override
    public boolean onBackPressed() {
        if (recyclerView != null &&
            recyclerView.getVisibility() == View.VISIBLE) {
            closeGrid();
            return true;
        }
        return false;
    }

    public interface Container { }
}
