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
package com.waz.zclient;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.waz.api.Permission;
import com.waz.api.PermissionProvider;
import com.waz.zclient.controllers.IControllerFactory;
import com.waz.zclient.core.stores.IStoreFactory;
import com.waz.zclient.pages.main.profile.preferences.PreferenceScreenStrategy;
import com.waz.zclient.utils.PermissionUtils;
import com.waz.zclient.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class BasePreferenceActivity extends BaseScalaActivity implements ServiceContainer,
                                                                                  PermissionProvider,
                                                                                  PreferenceFragmentCompat.OnPreferenceStartScreenCallback,
                                                                                  PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback,
                                                                                  PreferenceScreenStrategy.ReplaceFragment.Callbacks {

    private boolean started = false;
    private PreferenceScreenStrategy.ReplaceFragment replaceFragmentStrategy;
    private ScalaPermissionRequest permissionRequest;
    private Toolbar toolbar;
    private TextSwitcher titleSwitcher;
    private CharSequence title;

    @SuppressLint("PrivateResource")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        replaceFragmentStrategy = new PreferenceScreenStrategy.ReplaceFragment(this,
                                                                               R.anim.abc_fade_in,
                                                                               R.anim.abc_fade_out,
                                                                               R.anim.abc_fade_in,
                                                                               R.anim.abc_fade_out);

        toolbar = ViewUtils.getView(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        title = getTitle();
        titleSwitcher = new TextSwitcher(toolbar.getContext());
        titleSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new AppCompatTextView(toolbar.getContext());
                TextViewCompat.setTextAppearance(tv, R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
                return tv;
            }
        });
        titleSwitcher.setCurrentText(title);

        ab.setCustomView(titleSwitcher);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        titleSwitcher.setInAnimation(this, R.anim.abc_fade_in);
        titleSwitcher.setOutAnimation(this, R.anim.abc_fade_out);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (title == null) {
            return;
        }
        if (title.equals(this.title)) {
            return;
        }
        this.title = title;
        if (this.titleSwitcher != null) {
            this.titleSwitcher.setText(this.title);
        }
    }

    @Override
    public boolean onPreferenceStartScreen(final PreferenceFragmentCompat preferenceFragmentCompat,
                                           final PreferenceScreen preferenceScreen) {
        replaceFragmentStrategy.onPreferenceStartScreen(getSupportFragmentManager(),
                                                        preferenceFragmentCompat,
                                                        preferenceScreen);
        return true;
    }

    @Override
    public boolean onPreferenceDisplayDialog(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        getControllerFactory().setActivity(this);
        if (!started) {
            started = true;
            getStoreFactory().getZMessagingApiStore().getApi().onResume();
        }

        View contentView = ViewUtils.getView(getWindow().getDecorView(), android.R.id.content);
        if (contentView != null) {
            getControllerFactory().setGlobalLayout(contentView);
        }
    }

    @Override
    public void onStop() {
        if (started) {
            getStoreFactory().getZMessagingApiStore().getApi().onPause();
            started = false;
        }
        getStoreFactory().getZMessagingApiStore().getApi().setPermissionProvider(this);
        super.onStop();
    }

    @Override
    public void finish() {
        if (started) {
            getStoreFactory().getZMessagingApiStore().getApi().onPause();
            started = false;
        }
        getStoreFactory().getZMessagingApiStore().getApi().removePermissionProvider(this);
        super.finish();
    }

    @Override
    public final IStoreFactory getStoreFactory() {
        return ZApplication.from(this).getStoreFactory();
    }

    @Override
    public final IControllerFactory getControllerFactory() {
        return ZApplication.from(this).getControllerFactory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionRequest != null && requestCode == ScalaPermissionRequest.REQUEST_SE_PERMISSION) {
            if (grantResults.length < 0) {
                permissionRequest.cancel();
            } else if (PermissionUtils.verifyPermissions(grantResults)) {
                permissionRequest.grant();
            } else {
                final PermissionProvider.ResponseHandler responseHandler = permissionRequest.getTargetResponseHandler();
                if (responseHandler == null) {
                    return;
                }
                final Map<Permission, Permission.Status> resultMap = new HashMap<>();
                for (int i = 0; i < grantResults.length; i++) {
                    int result = grantResults[i];
                    Permission permission = Permission.forId(permissions[i]);
                    resultMap.put(permission, result == PackageManager.PERMISSION_GRANTED ? Permission.Status.GRANTED
                                                                                          : Permission.Status.DENIED);
                }
                responseHandler.handleResponse(resultMap);
            }
            permissionRequest = null;
        } else {
            /* child v4.fragments aren't receiving this due to bug. So forward to child fragments manually
             * https://code.google.com/p/android/issues/detail?id=189121
             * We need to do the bit operation because internally a an index is added to the requestCode.
             */
            getControllerFactory().getRequestPermissionsController().onRequestPermissionsResult(requestCode,
                                                                                                grantResults);
        }
    }

    @Override
    public void requestPermissions(Set<Permission> ps, PermissionProvider.ResponseHandler callback) {
        final ScalaPermissionRequest sePermissionRequest = new ScalaPermissionRequest(this, callback, ps);
        if (PermissionUtils.hasSelfPermissions(this, sePermissionRequest.getPermissionIds())) {
            sePermissionRequest.grant();
        } else {
            // TODO: Maybe use {@link PermissionUtils.shouldShowRequestPermissionRationale(this, sePermissionRequest.getPermissionIds())} to explain why we need those permissions
            permissionRequest = sePermissionRequest;
            sePermissionRequest.proceed();
        }
    }
}
