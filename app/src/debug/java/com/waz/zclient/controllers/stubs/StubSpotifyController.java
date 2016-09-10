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
package com.waz.zclient.controllers.stubs;

import android.app.Activity;
import android.content.Intent;
import com.spotify.sdk.android.player.Config;
import com.waz.zclient.controllers.spotify.ISpotifyController;
import com.waz.zclient.controllers.spotify.SpotifyObserver;
import java.lang.Override;

public class StubSpotifyController implements ISpotifyController {
  @Override
  public void addSpotifyObserver(SpotifyObserver observer) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void logout() {
    ;
  }

  @Override
  public boolean shouldShowLoginHint() {
    return false;
  }

  @Override
  public void setActivity(Activity activity) {
    ;
  }

  @Override
  public void handleActivityResult(int requestCode, int resultCode, Intent data) {
    ;
  }

  @Override
  public void login(Activity activity) {
    ;
  }

  @Override
  public Config getPlayerConfig() {
    return null;
  }

  @Override
  public boolean isLoggedIn() {
    return false;
  }

  @Override
  public void removeSpotifyObserver(SpotifyObserver observer) {
    ;
  }
}
