/*
 *  Copyright (C) 2019 RevengeOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.syberia.internal.util;

import android.content.Context;
import android.content.om.IOverlayManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

public class NavBarUtils {

    private static final String TAG = "NavBarUtils";

    public static boolean isBackHapticEnabled(Context context, int userId) {
        return Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.BACK_GESTURE_HAPTIC, 0, userId) == 1;
    }

}
