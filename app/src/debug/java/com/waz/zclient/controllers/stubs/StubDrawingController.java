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

import com.waz.api.ImageAsset;
import com.waz.zclient.controllers.drawing.DrawingObserver;
import com.waz.zclient.controllers.drawing.IDrawingController;
import java.lang.Override;

public class StubDrawingController implements IDrawingController {
  @Override
  public void showDrawing(ImageAsset image, IDrawingController.DrawingDestination drawingDestination) {
    ;
  }

  @Override
  public void tearDown() {
    ;
  }

  @Override
  public void removeDrawingObserver(DrawingObserver drawingObserver) {
    ;
  }

  @Override
  public void hideDrawing(IDrawingController.DrawingDestination drawingDestination, boolean imageSent) {
    ;
  }

  @Override
  public void addDrawingObserver(DrawingObserver drawingObserver) {
    ;
  }
}
