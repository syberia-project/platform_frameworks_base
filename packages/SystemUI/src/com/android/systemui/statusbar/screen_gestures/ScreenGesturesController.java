package com.android.systemui.statusbar.screen_gestures;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.gesture.EdgeGestureManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.android.internal.util.gesture.EdgeGesturePosition;
import com.android.internal.util.gesture.EdgeServiceConstants;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.internal.util.syberia.SyberiaUtils;

/**
 * Created by arasthel on 15/02/18.
 */

public class ScreenGesturesController {

    public static final boolean DEBUG = false;

    private static final String TAG = "ScreenGesturesControlle";

    private Context context;
    private WindowManager windowManager;
    private StatusBar statusBar;
    private KeyguardManager keyguardManager;

    private ScreenGesturesView screenGesturesView;

    private EdgeGestureManager edgeGestureManager = EdgeGestureManager.getInstance();

    private EdgeGestureManager.EdgeGestureActivationListener gestureManagerListener = new EdgeGestureManager.EdgeGestureActivationListener() {

        @Override
        public void onEdgeGestureActivation(int touchX, int touchY, EdgeGesturePosition position, int flags) {
            if (DEBUG) Log.d(TAG, "onEdgeGestureActivation: Starting gesture");
            final ScreenGesturesView gesturesView = screenGesturesView;

            if (gesturesView != null && !keyguardManager.isKeyguardLocked()) {
                boolean startGesture = false;
                String backSettingsId = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        Settings.Secure.EDGE_GESTURES_BACK_EDGES :
                        Settings.Secure.EDGE_GESTURES_LANDSCAPE_BACK_EDGES;
                int backGestureEdgesFlag = 0;
                int percent = 0;
                try {
                    backGestureEdgesFlag = Settings.Secure.getIntForUser(context.getContentResolver(),
                            backSettingsId,
                            UserHandle.USER_CURRENT);
                    percent = Settings.Secure.getIntForUser(context.getContentResolver(),
                            Settings.Secure.EDGE_GESTURES_BACK_SCREEN_PERCENT,
                            UserHandle.USER_CURRENT);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                if ((position.FLAG & backGestureEdgesFlag) != 0) {
                    Point displaySize = new Point();
                    windowManager.getDefaultDisplay().getSize(displaySize);
                       if (backGestureEdgesFlag == 1 && touchY < (percent*displaySize.y)/100 && touchX < (10*displaySize.y)/100) {
                            startGesture = true;
                       } else if (backGestureEdgesFlag == 4 && touchY < (percent*displaySize.y)/100 && touchX > (90*displaySize.y)/100) {
                            startGesture = true;
                       } else
                         startGesture = touchY < (percent*displaySize.y)/100;
                }
                if ((position.FLAG & EdgeGesturePosition.BOTTOM.FLAG) != 0)
                     startGesture = true;

                if (startGesture) {
                    gesturesView.startGesture(touchX, touchY, position);

                    handler.post(() -> {
                        gainTouchFocus(gesturesView.getWindowToken());
                        gestureManagerListener.restoreListenerState();
                    });
                } else {
                    gestureManagerListener.restoreListenerState();
                }
            } else {
                gestureManagerListener.restoreListenerState();
            }

        }
    };

    private ScreenGesturesView.OnGestureCompletedListener onGestureCompletedListener = gestureType -> {
        switch (gestureType) {
            case ScreenGesturesView.GestureType.HOME:
                SyberiaUtils.sendKeycode(KeyEvent.KEYCODE_HOME);
                break;
            case ScreenGesturesView.GestureType.BACK:
                SyberiaUtils.sendKeycode(KeyEvent.KEYCODE_BACK);
                break;
            case ScreenGesturesView.GestureType.RECENTS:
                SyberiaUtils.sendKeycode(KeyEvent.KEYCODE_APP_SWITCH);
                break;
            default:
                Log.e(TAG, "Unknown event");
                break;
        }

        gestureManagerListener.restoreListenerState();
    };

    private Handler handler = new Handler(Looper.getMainLooper());

    public ScreenGesturesController(Context context, WindowManager windowManager, StatusBar statusBar) {
        this.context = context;
        this.windowManager = windowManager;
        this.statusBar = statusBar;

        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        edgeGestureManager.setEdgeGestureActivationListener(gestureManagerListener);
        setupEdgeGestureManager();

        screenGesturesView = new ScreenGesturesView(context);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        windowManager.addView(screenGesturesView, params);

        screenGesturesView.setOnGestureCompletedListener(onGestureCompletedListener);
    }

    public void reorient() {
        setupEdgeGestureManager();
    }

    public void stop() {
        gestureManagerListener.restoreListenerState();
        edgeGestureManager.updateEdgeGestureActivationListener(gestureManagerListener, 0);
        windowManager.removeView(screenGesturesView);
        screenGesturesView = null;
    }

    private void setupEdgeGestureManager() {
        int sensitivity = EdgeServiceConstants.SENSITIVITY_HIGHEST;

        int edges = (EdgeGesturePosition.BOTTOM.FLAG | EdgeGesturePosition.RIGHT.FLAG | EdgeGesturePosition.LEFT.FLAG);
        edgeGestureManager.updateEdgeGestureActivationListener(gestureManagerListener,
                sensitivity << EdgeServiceConstants.SENSITIVITY_SHIFT
                | edges
                | EdgeServiceConstants.LONG_LIVING
                | EdgeServiceConstants.UNRESTRICTED);
    }
}