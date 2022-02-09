package com.syberia.android.systemui;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;

import com.syberia.android.systemui.dagger.SyberiaGlobalRootComponent;
import com.syberia.android.systemui.dagger.SyberiaSysUIComponent;
import com.syberia.android.systemui.dagger.DaggerSyberiaGlobalRootComponent;

import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.GlobalRootComponent;
import com.android.systemui.navigationbar.gestural.BackGestureTfClassifierProvider;
import com.android.systemui.screenshot.ScreenshotNotificationSmartActionsProvider;

import com.google.android.systemui.gesture.BackGestureTfClassifierProviderGoogle;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class SyberiaSystemUIFactory extends SystemUIFactory {
    @Override
    protected GlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerSyberiaGlobalRootComponent.builder()
                .context(context)
                .build();
    }
    @Override
    public BackGestureTfClassifierProvider createBackGestureTfClassifierProvider(AssetManager am, String modelName) {
        return new BackGestureTfClassifierProviderGoogle(am, modelName);
    }

    @Override
    public void init(Context context, boolean fromTest) throws ExecutionException, InterruptedException {
        super.init(context, fromTest);
        if (shouldInitializeComponents()) {
            ((SyberiaSysUIComponent) getSysUIComponent()).createKeyguardSmartspaceController();
        }
    }
}
