package com.syberia.android.systemui;

import android.content.Context;

import com.syberia.android.systemui.dagger.SyberiaGlobalRootComponent;
import com.syberia.android.systemui.dagger.DaggerSyberiaGlobalRootComponent;

import com.android.systemui.SystemUIFactory;
import com.android.systemui.dagger.GlobalRootComponent;

public class SyberiaSystemUIFactory extends SystemUIFactory {
    @Override
    protected GlobalRootComponent buildGlobalRootComponent(Context context) {
        return DaggerSyberiaGlobalRootComponent.builder()
                .context(context)
                .build();
    }
}
