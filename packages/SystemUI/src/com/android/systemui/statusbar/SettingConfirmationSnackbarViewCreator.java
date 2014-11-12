/*
 * Copyright 2016 ParanoidAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.android.systemui.R;

/**
 * Snackbar view creator for use with OnTheSpot.
 */
public class SettingConfirmationSnackbarViewCreator {

    private Context mContext;
    private SettingConfirmationSnackbarView mSnackbarView;
    private WindowManager mWindowManager;

    public SettingConfirmationSnackbarViewCreator(Context context) {
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(
                Context.WINDOW_SERVICE);
    }

    private void initSnackBar() {
        ContextThemeWrapper themedContext = new ContextThemeWrapper(mContext, R.style.qs_theme);
        mSnackbarView = (SettingConfirmationSnackbarView) View.inflate(themedContext,
                R.layout.setting_confirmation_snackbar, null);
        if (mSnackbarView != null) attachSnackBar();
    }

    private void attachSnackBar() {
        if (mSnackbarView != null) {
            final WindowManager.LayoutParams snackbarLp = new WindowManager.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_NAVIGATION_BAR_PANEL,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            snackbarLp.gravity = Gravity.BOTTOM;
            mWindowManager.addView(mSnackbarView, snackbarLp);
        }
    }

    public SettingConfirmationSnackbarView getSnackbarView() {
        initSnackBar();
        return mSnackbarView;
    }
}
