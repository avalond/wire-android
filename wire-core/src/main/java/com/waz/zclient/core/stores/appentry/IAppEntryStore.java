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
package com.waz.zclient.core.stores.appentry;

import android.os.Bundle;
import com.waz.api.AccentColor;
import com.waz.api.ImageAsset;
import com.waz.api.Invitations;
import com.waz.api.Self;
import com.waz.zclient.core.controllers.tracking.attributes.RegistrationEventContext;

public interface IAppEntryStore {

    boolean onBackPressed();

    void tearDown();

    interface ErrorCallback {
        void onError(AppEntryError error);
    }

    interface SuccessCallback {
        void onSuccess();
    }

    void setState(AppEntryState state);
    void triggerStateUpdate();
    void setCallback(AppEntryStateCallback callback);
    void onSaveInstanceState(Bundle outState);
    void onRestoreInstanceState(Bundle savedInstanceState, Self self);
    AppEntryState getEntryPoint();

    void resumeAppEntry(Self self, String personalInvitationToken);
    void clearCurrentState();
    void clearSavedUserInput();

    void setRegistrationPhone(String countryCode, String phone, ErrorCallback errorCallback);
    void setSignInPhone(String countryCode, String phone, ErrorCallback errorCallback);
    void submitCode(String phoneVerificationCode, ErrorCallback errorCallback);
    void registerWithPhone(String name, AccentColor accentColor, ErrorCallback errorCallback);
    void setPhonePicture(ImageAsset imageAsset);
    void addEmailAndPasswordToPhone(String email,
                                    String password,
                                    ErrorCallback emailErrorCallback,
                                    ErrorCallback passwordErrorCallback);
    void setEmailPicture(ImageAsset imageAsset);
    void registerWithEmail(String email,
                           String password,
                           String name,
                           AccentColor accentColor,
                           ErrorCallback errorCallback);

    void acceptEmailInvitation(String password, AccentColor accentColor);
    void acceptPhoneInvitation(AccentColor accentColor);

    void signInWithEmail(String email, String password, ErrorCallback errorCallback);
    void addPhoneToEmail(String countryCode, String phone, ErrorCallback errorCallback);
    void resendEmail();
    void resendPhone(SuccessCallback successCallback, ErrorCallback errorCallback);
    void triggerVerificationCodeCallToUser(SuccessCallback successCallback, ErrorCallback errorCallback);

    String getCountryCode();
    String getPhone();
    String getEmail();
    String getPassword();
    String getName();
    String getUserId();

    String getInvitationEmail();
    String getInvitationPhone();
    String getInvitationName();
    Invitations.PersonalToken getInvitationToken();

    void setRegistrationContext(RegistrationEventContext registrationEventContext);
    RegistrationEventContext getPhoneRegistrationContext();
    RegistrationEventContext getEmailRegistrationContext();

}
