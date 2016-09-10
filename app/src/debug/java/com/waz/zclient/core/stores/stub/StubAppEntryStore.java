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
package com.waz.zclient.core.stores.stub;

import android.os.Bundle;
import com.waz.api.AccentColor;
import com.waz.api.ImageAsset;
import com.waz.api.Invitations;
import com.waz.api.Self;
import com.waz.zclient.core.controllers.tracking.attributes.RegistrationEventContext;
import com.waz.zclient.core.stores.appentry.AppEntryState;
import com.waz.zclient.core.stores.appentry.AppEntryStateCallback;
import com.waz.zclient.core.stores.appentry.IAppEntryStore;
import java.lang.Override;
import java.lang.String;

public class StubAppEntryStore implements IAppEntryStore {
  @Override
  public void signInWithEmail(String email, String password, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public String getInvitationPhone() {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String getInvitationEmail() {
    return null;
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState, Self self) {
    ;
  }

  @Override
  public void triggerVerificationCodeCallToUser(IAppEntryStore.SuccessCallback successCallback, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    ;
  }

  @Override
  public void registerWithEmail(String email, String password, String name, AccentColor accentColor, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public String getCountryCode() {
    return null;
  }

  @Override
  public void triggerStateUpdate() {
    ;
  }

  @Override
  public void setPhonePicture(ImageAsset imageAsset) {
    ;
  }

  @Override
  public String getPhone() {
    return null;
  }

  @Override
  public void clearSavedUserInput() {
    ;
  }

  @Override
  public void resendPhone(IAppEntryStore.SuccessCallback successCallback, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void setEmailPicture(ImageAsset imageAsset) {
    ;
  }

  @Override
  public void setRegistrationPhone(String countryCode, String phone, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void addEmailAndPasswordToPhone(String email, String password, IAppEntryStore.ErrorCallback emailErrorCallback, IAppEntryStore.ErrorCallback passwordErrorCallback) {
    ;
  }

  @Override
  public void acceptEmailInvitation(String password, AccentColor accentColor) {
    ;
  }

  @Override
  public AppEntryState getEntryPoint() {
    return null;
  }

  @Override
  public void resendEmail() {
    ;
  }

  @Override
  public RegistrationEventContext getEmailRegistrationContext() {
    return null;
  }

  @Override
  public void submitCode(String phoneVerificationCode, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void registerWithPhone(String name, AccentColor accentColor, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void setRegistrationContext(RegistrationEventContext registrationEventContext) {
    ;
  }

  @Override
  public void clearCurrentState() {
    ;
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  @Override
  public Invitations.PersonalToken getInvitationToken() {
    return null;
  }

  @Override
  public void resumeAppEntry(Self self, String personalInvitationToken) {
    ;
  }

  @Override
  public void setState(AppEntryState state) {
    ;
  }

  @Override
  public void acceptPhoneInvitation(AccentColor accentColor) {
    ;
  }

  @Override
  public RegistrationEventContext getPhoneRegistrationContext() {
    return null;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public String getEmail() {
    return null;
  }

  @Override
  public void addPhoneToEmail(String countryCode, String phone, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void setSignInPhone(String countryCode, String phone, IAppEntryStore.ErrorCallback errorCallback) {
    ;
  }

  @Override
  public void setCallback(AppEntryStateCallback callback) {
    ;
  }

  @Override
  public String getUserId() {
    return null;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getInvitationName() {
    return null;
  }
}
