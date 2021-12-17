package com.syberia.android.systemui.dagger;

import com.android.systemui.dagger.DefaultComponentBinder;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.SystemUIBinder;
import com.android.systemui.dagger.SysUIComponent;
import com.android.systemui.dagger.SystemUIModule;

import com.syberia.android.systemui.columbus.ColumbusModule;
import com.syberia.android.systemui.keyguard.SyberiaKeyguardSliceProvider;
import com.syberia.android.systemui.smartspace.KeyguardSmartspaceController;
import com.syberia.android.systemui.elmyra.ElmyraModule;

import dagger.Subcomponent;

@SysUISingleton
@Subcomponent(modules = {
        ColumbusModule.class,
        DefaultComponentBinder.class,
        DependencyProvider.class,
        ElmyraModule.class,
        SyberiaSystemUIBinder.class,
        SystemUIModule.class,
        SyberiaSystemUIModule.class})
public interface SyberiaSysUIComponent extends SysUIComponent {
    @SysUISingleton
    @Subcomponent.Builder
    interface Builder extends SysUIComponent.Builder {
        SyberiaSysUIComponent build();
    }

    /**
     * Member injection into the supplied argument.
     */
    void inject(SyberiaKeyguardSliceProvider keyguardSliceProvider);

    @SysUISingleton
    KeyguardSmartspaceController createKeyguardSmartspaceController();
}
