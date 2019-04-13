/*
 * Copyright (C) 2019 RevengeOS
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

package com.android.systemui.qs.tiles;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.MediaStore;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import android.provider.CalendarContract;

import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.qs.GlobalSetting;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import android.content.ComponentName;

import com.android.systemui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecorderTile extends QSTileImpl<BooleanState> {
    private final String mRecorderPackage = "org.lineageos.recorder";
    private final PackageManager mPackageManager;
    private final ActivityStarter mActivityStarter;

    public RecorderTile(QSHost host) {
        super(host);
        mPackageManager = mContext.getPackageManager();
        mActivityStarter = Dependency.get(ActivityStarter.class);
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    private void startRecorder() {
        Intent launchIntent = mPackageManager.getLaunchIntentForPackage(mRecorderPackage);
        if (launchIntent != null) {
            mActivityStarter.postStartActivityDismissingKeyguard(launchIntent, 0);
        }
    }

    private CharSequence getPackageName() {
        CharSequence mRecorderLabel = mContext.getString(R.string.quick_settings_recorder_label);
        try {
            mRecorderLabel = mPackageManager.getApplicationInfo(mRecorderPackage, 0).loadLabel(mPackageManager);
        } catch (NameNotFoundException e) {
        }
        return mRecorderLabel;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SYBERIA;
    }

    @Override
    public CharSequence getTileLabel() {
        return getPackageName();
    }

    @Override
    protected void handleClick() {
        mHost.collapsePanels();
        startRecorder();
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }


    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        state.icon = ResourceIcon.get(R.drawable.ic_qs_recorder);
        state.label = getPackageName();
        state.state = 1;
    }

    @Override
    public void handleSetListening(boolean listening) {
    }
}
