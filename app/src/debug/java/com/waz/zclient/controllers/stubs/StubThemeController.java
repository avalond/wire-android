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

import com.waz.zclient.controllers.theme.IThemeController;
import com.waz.zclient.controllers.theme.ThemeObserver;
import com.waz.zclient.ui.theme.OptionsTheme;
import java.lang.Override;

public class StubThemeController implements IThemeController {
  @Override
  public boolean isDarkTheme() {
    return false;
  }

  @Override
  public int getTheme() {
    return 0;
  }

  @Override
  public boolean isRestartPending() {
    return false;
  }

  @Override
  public void removePendingRestart() {
    ;
  }

  @Override
  public OptionsTheme getThemeDependentOptionsTheme() {
    return null;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void removeThemeObserver(ThemeObserver themeObserver) {
    ;
  }

  @Override
  public void toggleTheme(boolean fromPreferences) {
    ;
  }

  @Override
  public void addThemeObserver(ThemeObserver themeObserver) {
    ;
  }

  @Override
  public OptionsTheme getOptionsLightTheme() {
    return null;
  }

  @Override
  public void toggleThemePending(boolean fromPreferences) {
    ;
  }

  @Override
  public OptionsTheme getOptionsDarkTheme() {
    return null;
  }
}
