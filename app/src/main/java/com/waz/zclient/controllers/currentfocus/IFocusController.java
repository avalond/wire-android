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
package com.waz.zclient.controllers.currentfocus;

import android.content.Context;
import android.support.annotation.IntDef;
import com.waz.zclient.controllers.navigation.Page;

public interface IFocusController {

    @IntDef({CONVERSATION_CURSOR,
             CONVERSATION_LIST_SEARCHBOX,
             PARTICIPANTS_SEARCHBOX,
             CONVERSATION_EDIT_NAME,
             SELF_PROFILE_EDIT_NAME,
             SELF_PROFILE_EDIT_EMAIL,
             SELF_PROFILE_EDIT_PHONE,
             CONTACT_LIST_SEARCH,
             NONE
    })
    @interface FocusLocation { }
    int CONVERSATION_CURSOR = 0;
    int CONVERSATION_LIST_SEARCHBOX = 1;
    int PARTICIPANTS_SEARCHBOX = 2;
    int CONVERSATION_EDIT_NAME = 4;
    int SELF_PROFILE_EDIT_NAME = 5;
    int SELF_PROFILE_EDIT_EMAIL = 6;
    int SELF_PROFILE_EDIT_PHONE = 7;
    int CONTACT_LIST_SEARCH = 8;
    int NONE = 9;

    void tearDown();

    void addFocusObserver(FocusObserver focusObserver);

    void removeFocusObserver(FocusObserver focusObserver);

    void setFocus(@FocusLocation int currentFocus);

    @FocusLocation int getCurrentFocus();

    void restoreToNextFocus(Context context, int pagerPosition, Page currentPage, boolean conversationlistSearchIsOpen);
}
