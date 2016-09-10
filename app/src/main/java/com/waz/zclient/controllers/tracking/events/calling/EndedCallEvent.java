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
package com.waz.zclient.controllers.tracking.events.calling;

import android.support.annotation.NonNull;
import com.waz.api.CallDropped;
import com.waz.api.CallEnded;
import com.waz.zclient.core.controllers.tracking.attributes.Attribute;
import com.waz.zclient.core.controllers.tracking.attributes.RangedAttribute;
import com.waz.zclient.core.controllers.tracking.events.Event;

public class EndedCallEvent extends Event {

    private static final String DROP_REASON_GSM = "gsm_call";
    private static final String DROP_REASON_WIFI = "drop_wifi";
    private static final String DROP_REASON_4G = "drop_4g";
    private static final String DROP_REASON_3G = "drop_3g";
    private static final String DROP_REASON_EDGE = "drop_EDGE";
    private static final String DROP_REASON_2G = "drop_2g";

    public EndedCallEvent(CallEnded callEnded) {
        attributes.put(Attribute.CONVERSATION_TYPE, callEnded.kindOfCall().toString());
        attributes.put(Attribute.CALLING_DIRECTION, callEnded.direction().toString());
        attributes.put(Attribute.CALLING_END_REASON, callEnded.cause().toString());
        rangedAttributes.put(RangedAttribute.VOICE_CALL_DURATION, (int) callEnded.duration().getSeconds());
        attributes.put(Attribute.CALLING_CONVERSATION_PARTICIPANTS, String.valueOf(callEnded.numConvMembers()));
        attributes.put(Attribute.WITH_OTTO, String.valueOf(callEnded.isOtto()));
    }

    public EndedCallEvent(CallDropped callDropped) {
        attributes.put(Attribute.CONVERSATION_TYPE, callDropped.kindOfCall().toString());
        attributes.put(Attribute.CALLING_DIRECTION, callDropped.direction().toString());
        switch (callDropped.dropCause()) {
            case INTERRUPTED:
                // Interrupted by GSM call
                attributes.put(Attribute.CALLING_END_REASON, DROP_REASON_GSM);
                break;
            default:
                switch (callDropped.networkMode()) {
                    case WIFI:
                        attributes.put(Attribute.CALLING_END_REASON, DROP_REASON_WIFI);
                        break;
                    case _4G:
                        attributes.put(Attribute.CALLING_END_REASON, DROP_REASON_4G);
                        break;
                    case _3G:
                        attributes.put(Attribute.CALLING_END_REASON, DROP_REASON_3G);
                        break;
                    case EDGE:
                        attributes.put(Attribute.CALLING_END_REASON, DROP_REASON_EDGE);
                        break;
                    case _2G:
                        attributes.put(Attribute.CALLING_END_REASON, DROP_REASON_2G);
                        break;
                }
                break;
        }

        rangedAttributes.put(RangedAttribute.VOICE_CALL_DURATION, (int) callDropped.duration().getSeconds());
        attributes.put(Attribute.CALLING_CONVERSATION_PARTICIPANTS, String.valueOf(callDropped.numConvMembers()));
        attributes.put(Attribute.WITH_OTTO, String.valueOf(callDropped.isOtto()));
    }

    @NonNull
    @Override
    public String getName() {
        return "calling.ended_call";
    }
}
