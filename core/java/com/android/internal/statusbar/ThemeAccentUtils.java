/*
 * Copyright (C) 2018-2021 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.statusbar;

import android.app.ActivityManager;
import android.content.om.OverlayManager;
import android.content.om.OverlayInfo;
import android.os.UserHandle;

public class ThemeAccentUtils {

    public static final String TAG = "ThemeAccentUtils";

    // Rounded corner styles
    private static final String[] ROUNDED_STYLES = {
        "com.android.system.rounded.stock", // 0
        "com.android.system.rounded.none", // 1
        "com.android.system.rounded.slight", // 2
        "com.android.system.rounded.medium", // 3
        "com.android.system.rounded.high", // 4
        "com.android.system.rounded.extreme", // 5
    };

    // Unloads the rounded styles
    private static void unloadRoundedStyle(OverlayManager om, UserHandle userId) {
        for (String style : ROUNDED_STYLES) {
            try {
                om.setEnabled(style, false, userId);
            } catch (Exception e) {
            }
        }
    }

    // Set rounded style
    public static void setRoundedStyle(OverlayManager om, int roundedStyle) {
        UserHandle userId = UserHandle.of(ActivityManager.getCurrentUser());

        // Always unload rounded styles
        unloadRoundedStyle(om, userId);

        if (roundedStyle == 0) return;

        try {
            om.setEnabled(ROUNDED_STYLES[roundedStyle], true, userId);
        } catch (Exception e) {
        }
    }
}
