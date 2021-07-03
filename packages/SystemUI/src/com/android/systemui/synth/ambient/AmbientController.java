package com.android.systemui.synth.ambient;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.BitmapShader;
import android.graphics.LinearGradient;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.MathUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.util.ImageUtils;
import com.android.internal.util.Converter;
import com.android.systemui.Dependency;
import com.android.systemui.pulse.ColorAnimator;
import com.android.systemui.R;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AmbientController implements ColorAnimator.ColorAnimationListener {

    private static final int ANIMATION_DURATION = 500;

    private Context mContext;
    private Handler mHandler;
    private SettingsObserver mSettingsObserver;

    private NotificationPanelViewController mNotificationPanelViewController;

    private FrameLayout mContainer;
    private ConstraintLayout mTextContainer;
    private ConstraintSet mTextConstraintSet = new ConstraintSet();
    private TextView mTextView;

    private String mJson;

    private boolean mTextState;
    private String mCustomText;
    private int mTextColorType;
    private int mTextColorStart;
    private int mTextColorFinal;
    private String mTextBackground;
    private int mTextVerticalPosition;
    private int mTextHorizontalPosition;
    private int mTextShowType;
    private int mTextHideType;
    private int mTextFont;
    private int mTextSize;
    private int mTextRotation;
    private int mTextLetterSpacing;
    private int mTextMode;
    
    private boolean mTextShadowEnabled;
    private int mTextShadowDx;
    private int mTextShadowDy;
    private int mTextShadowColor;
    private int mTextShadowRadius;

    private boolean mVisibility;
    private boolean mKeyguardShowing;
    private boolean mQSShowing;
    private boolean mDozing;
    
    private ColorAnimator mLavaLamp;
    private boolean mColorTypeLavaLamp;

    public AmbientController(
            Context context, 
            FrameLayout container,
            ConstraintLayout textContainer,
            TextView textView,
            NotificationPanelViewController notificationPanelViewController) {
        mContext = context;
        mContainer = container;
        mTextContainer = textContainer;
        mTextView = textView;

        mTextConstraintSet.clone(mTextContainer);

        mLavaLamp = new ColorAnimator();
        mLavaLamp.setColorAnimatorListener(this);
        mLavaLamp.setAnimationTime(10000);

        mHandler = new Handler();
        mSettingsObserver = new SettingsObserver(mHandler);
        mSettingsObserver.register();
        mSettingsObserver.updateSettings();

        mNotificationPanelViewController = notificationPanelViewController;

    }

    public void setDozing(boolean dozing) {
        if (mDozing != dozing) {
            mDozing = dozing;
            updateVisibility();
        }
    }

    public void setKeyguardShowing(boolean showing) {
        if (mKeyguardShowing != showing) {
            mKeyguardShowing = showing;
            updateVisibility();
        }
    }

    public void setQSShowing(boolean showing) {
        if (showing != mQSShowing) {
            mQSShowing = showing;
            updateVisibility();
        }
    }

    private void updateVisibility() {

        boolean textInQuickSettings = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.AMBIENT_TEXT_QS, 0, UserHandle.USER_CURRENT) != 0;
        boolean textInAmbient = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.AMBIENT_TEXT_AOD, 1, UserHandle.USER_CURRENT) != 0;
        boolean textInLockscreen = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.AMBIENT_TEXT_LOCKSCREEN, 0, UserHandle.USER_CURRENT) != 0;

        boolean showInQuickSettings = mQSShowing && !mDozing;
        boolean showInAmbient = mKeyguardShowing && mDozing;
        boolean showInLockscreen = mKeyguardShowing && !mDozing;

        boolean ambientLightsHideAod = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.AOD_NOTIFICATION_PULSE_CLEAR, 0, UserHandle.USER_CURRENT) != 0;
        boolean pulseLights = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.NOTIFICATION_PULSE, 0, UserHandle.USER_CURRENT) != 0;
        boolean hideAodContent = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.AMBIENT_HIDE_KEYGUARD, 0, UserHandle.USER_CURRENT) == 1;

        if (showInQuickSettings) {
            if (mTextState && textInQuickSettings) showText(); else hideText();
        } else if (showInAmbient) {
            if (mTextState && textInAmbient) showText(); else hideText();
            if (mTextState) {
                mNotificationPanelViewController.updateAodContent(hideAodContent, (pulseLights && ambientLightsHideAod) ? false : true);
            } else {
                mNotificationPanelViewController.updateAodContent(false, (pulseLights && ambientLightsHideAod) ? false : true);
            }
        } else if (showInLockscreen) {
            if (mTextState && textInLockscreen) showText(); else hideText();
        } else {
            hideText();
        }
    }

    private void showText() {
        TransitionSet transition = new TransitionSet();
        mTextView.setTranslationX(0);

        ValueAnimator animationTextAlpha = ValueAnimator.ofFloat(0f, 1f);
        animationTextAlpha.setDuration(ANIMATION_DURATION);
        animationTextAlpha.setInterpolator(new FastOutSlowInInterpolator());
        animationTextAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextView.setAlpha((float) animation.getAnimatedValue());
                mTextView.invalidate();
            }
        });
        switch (mTextShowType) {
            case 0:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Fade())
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 1:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Slide(Gravity.LEFT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 2:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Slide(Gravity.RIGHT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 3:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Fade())
                    .addTransition(new Slide(Gravity.LEFT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 4:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Fade())
                    .addTransition(new Slide(Gravity.RIGHT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
        }
    
        TransitionManager.beginDelayedTransition((ViewGroup) mTextContainer, transition);
        // mContainer.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        animationTextAlpha.start();
        mVisibility = true;
    }

    private void hideText() {
        TransitionSet transition = new TransitionSet();

        ValueAnimator animationTextAlpha = ValueAnimator.ofFloat(1f, 0f);
        animationTextAlpha.setDuration(ANIMATION_DURATION);
        animationTextAlpha.setInterpolator(new FastOutSlowInInterpolator());
        animationTextAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                mTextView.setAlpha((float) animation.getAnimatedValue());
                mTextView.invalidate();
            }
        });
        switch (mTextHideType) {
            case 0:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Fade())
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 1:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Slide(Gravity.LEFT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 2:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Slide(Gravity.RIGHT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 3:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Fade())
                    .addTransition(new Slide(Gravity.LEFT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
            case 4:
                transition = new TransitionSet()
                    .addTarget(mTextView)
                    .addTransition(new Fade())
                    .addTransition(new Slide(Gravity.RIGHT))
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(ANIMATION_DURATION);
                break;
        }
    
        TransitionManager.beginDelayedTransition((ViewGroup) mTextContainer, transition);
        // mContainer.setVisibility(View.INVISIBLE);
        animationTextAlpha.start();
        mTextView.setVisibility(View.GONE);
        mVisibility = false;
    }

    private void updateText() {
        if (!mTextState) return;

        setTextMode();
        mTextView.setTextSize(mTextSize);
        mTextView.setTypeface(getFont(mTextFont));
        mTextView.setLetterSpacing((float) (((float) mTextLetterSpacing) / 100f));

        mTextConstraintSet.setHorizontalBias(mTextView.getId(), (((float) mTextHorizontalPosition) / 100f));
        mTextConstraintSet.setVerticalBias(mTextView.getId(), (((float) mTextVerticalPosition) / 100f));
        mTextConstraintSet.setRotation(mTextView.getId(), mTextRotation);
        mTextConstraintSet.applyTo(mTextContainer);
        updateColor();
        updateShadow();
    }

    private void updateShadow() {
        float radius = (float) (mTextShadowRadius / 25);
        float dx = (float) (mTextShadowDx / 5);
        float dy = (float) (mTextShadowDy / 5);
        mTextView.setShadowLayer(mTextShadowEnabled ? radius : 0f, dx, dy, mTextShadowColor);
    }

    private void updateColor() {
        if (!mTextState) return;
        mColorTypeLavaLamp = false;

        switch (mTextColorType) {
            case 0: // Color Accent
                mTextView.getPaint().setShader(null);
                mTextView.setTextColor(getAccentColor());
                break;
            case 1: // Custom Color
                mTextView.getPaint().setShader(null);
                mTextView.setTextColor(mTextColorStart);
                break;
            case 2: // Lava Lamp
                mTextView.getPaint().setShader(null);
                mLavaLamp = new ColorAnimator();
                mLavaLamp.setColorAnimatorListener(this);
                mLavaLamp.setAnimationTime(10000);
                mLavaLamp.start();
                mColorTypeLavaLamp = true;
                break;
            case 3: // Lava Lamp Custom Colors
                mTextView.getPaint().setShader(null);
                mLavaLamp = new ColorAnimator(ValueAnimator.ofFloat(0, 1), 10000, mTextColorStart, mTextColorFinal);
                mLavaLamp.setColorAnimatorListener(this);
                mLavaLamp.start();
                mColorTypeLavaLamp = true;
                break;
            case 4: // Gradient 
                LinearGradient shader = new LinearGradient(0f, 0f, mTextView.getWidth(), mTextView.getHeight(), mTextColorStart, mTextColorFinal, Shader.TileMode.CLAMP);
                mTextView.getPaint().setShader(shader);
                break;
        }
    }

    private void setTextMode() {
        switch (mTextMode) {
            case 0:
                mTextView.setText(mCustomText);
                break;
            case 1:
                mTextView.setText(new SimpleDateFormat("hh:mm").format(new Date()));
                break;
            case 2:
                mTextView.setText(new SimpleDateFormat("EEE, MMM d").format(new Date()));
                break;
            case 3:
                String title = Dependency.get(NotificationMediaManager.class).getNowPlayingTrack();
                mTextView.setText(title != null ? (title.isEmpty() ? mCustomText : title) : mCustomText);
                break;
        }
    }

    public boolean getState() {
        return (mTextState);
    }

    @Override
    public void onColorChanged(ColorAnimator colorAnimator, int color) {
        if (mColorTypeLavaLamp) {
            mTextView.setTextColor(color);
        }
    }

    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        void register() {
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_ENABLED), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_CUSTOM_TEXT), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_COLOR_TYPE), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_CUSTOM_COLOR_START), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_CUSTOM_COLOR_FINAL), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_GRAVITY), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_VERTICAL_LOCATION), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_HORIZONTAL_LOCATION), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_HIDE_TYPE), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHOW_TYPE), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_ROTATION), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_FONT), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SIZE), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_LETTER_SPACING), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_ENABLED), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_DX), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_DY), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_COLOR), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_RADIUS), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_MODE), false, this,
                    UserHandle.USER_ALL);
            mContext.getContentResolver().registerContentObserver(
                    Settings.System.getUriFor(Settings.System.AMBIENT_HIDE_KEYGUARD), false, this,
                    UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            if (uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_GRAVITY)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_VERTICAL_LOCATION)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_HORIZONTAL_LOCATION)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_ROTATION))) {
                updateTextPosition();
            } else if (uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_ENABLED)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_CUSTOM_TEXT)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHOW_TYPE)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_HIDE_TYPE)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_FONT)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SIZE)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_LETTER_SPACING)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_MODE))) {
                updateTextVar();
            } else if (uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_COLOR_TYPE)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_CUSTOM_COLOR_START)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_CUSTOM_COLOR_FINAL))) {
                updateTextColor();
            } else if (uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_ENABLED)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_DX)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_DY)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_COLOR)) ||
                        uri.equals(Settings.System.getUriFor(Settings.System.AMBIENT_TEXT_SHADOW_RADIUS))) {
                updateTextShadow();
            }
        }

        void updateSettings() {
            updateTextPosition();
            updateTextVar();
            updateTextColor();
            updateTextShadow();
            updateText();
        }

        void updateTextPosition() {
            mTextVerticalPosition = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_VERTICAL_LOCATION, 50, UserHandle.USER_CURRENT);
            mTextHorizontalPosition = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_HORIZONTAL_LOCATION, 50, UserHandle.USER_CURRENT);
            mTextRotation = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_ROTATION, 0, UserHandle.USER_CURRENT);
            updateText();
        }

        void updateTextVar() {
            mTextState = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_ENABLED, 0, UserHandle.USER_CURRENT) == 1;
            mCustomText = Settings.System.getStringForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_CUSTOM_TEXT, UserHandle.USER_CURRENT);
            mTextShowType = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SHOW_TYPE, 0, UserHandle.USER_CURRENT);
            mTextHideType = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_HIDE_TYPE, 0, UserHandle.USER_CURRENT);
            mTextFont = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_FONT, 0, UserHandle.USER_CURRENT);
            mTextSize = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SIZE, 24, UserHandle.USER_CURRENT);
            mTextLetterSpacing = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_LETTER_SPACING, 1, UserHandle.USER_CURRENT);
            mTextMode = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_MODE, 0, UserHandle.USER_CURRENT);
            updateText();
        }

        void updateTextColor() {
            mTextColorType = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_COLOR_TYPE, 0, UserHandle.USER_CURRENT);
            mTextColorStart = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_CUSTOM_COLOR_START, getAccentColor(), UserHandle.USER_CURRENT);
            mTextColorFinal = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_CUSTOM_COLOR_FINAL, getAccentColor(), UserHandle.USER_CURRENT);
            updateColor();
        }

        void updateTextShadow() {
            mTextShadowEnabled = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SHADOW_ENABLED, 0, UserHandle.USER_CURRENT) != 0;
            mTextShadowDx = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SHADOW_DX, 0, UserHandle.USER_CURRENT);
            mTextShadowDy = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SHADOW_DY, 0, UserHandle.USER_CURRENT);
            mTextShadowColor = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SHADOW_COLOR, getAccentColor(), UserHandle.USER_CURRENT);
            mTextShadowRadius = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.AMBIENT_TEXT_SHADOW_RADIUS, 25, UserHandle.USER_CURRENT);
            updateShadow();
        }
    };

    int getAccentColor() {
        final TypedValue value = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.colorAccent, value, true);
        return value.data;
    }

    private Typeface getFont(int userSelection) {
        Typeface tf;
        switch (userSelection) {
            case 0:
            default:
                return Typeface.create(mContext.getResources().getString(R.string.clock_sysfont_headline), Typeface.NORMAL);
            case 1:
                return Typeface.create(mContext.getResources().getString(R.string.clock_sysfont_body), Typeface.NORMAL);
            case 2:
                return Typeface.create("sans-serif", Typeface.BOLD);
            case 3:
                return Typeface.create("sans-serif", Typeface.NORMAL);
            case 4:
                return Typeface.create("sans-serif", Typeface.ITALIC);
            case 5:
                return Typeface.create("sans-serif", Typeface.BOLD_ITALIC);
            case 6:
                return Typeface.create("sans-serif-light", Typeface.NORMAL);
            case 7:
                return Typeface.create("sans-serif-thin", Typeface.NORMAL);
            case 8:
                return Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            case 9:
                return Typeface.create("sans-serif-condensed", Typeface.ITALIC);
            case 10:
                return Typeface.create("sans-serif-condensed", Typeface.BOLD);
            case 11:
                return Typeface.create("sans-serif-condensed", Typeface.BOLD_ITALIC);
            case 12:
                return Typeface.create("sans-serif-medium", Typeface.NORMAL);
            case 13:
                return Typeface.create("sans-serif-medium", Typeface.ITALIC);
            case 14:
                return Typeface.create("abelreg", Typeface.NORMAL);
            case 15:
                return Typeface.create("adamcg-pro", Typeface.NORMAL);
            case 16:
                return Typeface.create("adventpro", Typeface.NORMAL);
            case 17:
                return Typeface.create("alien-league", Typeface.NORMAL);
            case 18:
                return Typeface.create("archivonar", Typeface.NORMAL);
            case 19:
                return Typeface.create("autourone", Typeface.NORMAL);
            case 20:
                return Typeface.create("badscript", Typeface.NORMAL);
            case 21:
                return Typeface.create("bignoodle-regular", Typeface.NORMAL);
            case 22:
                return Typeface.create("biko", Typeface.NORMAL);
            case 23:
                return Typeface.create("cherryswash", Typeface.NORMAL);
            case 24:
                return Typeface.create("ginora-sans", Typeface.NORMAL);
            case 25:
                return Typeface.create("googlesans-sys", Typeface.NORMAL);
            case 26:
                return Typeface.create("ibmplex-mono", Typeface.NORMAL);
            case 27:
                return Typeface.create("inkferno", Typeface.NORMAL);
            case 28:
                return Typeface.create("instruction", Typeface.NORMAL);
            case 29:
                return Typeface.create("jack-lane", Typeface.NORMAL);
            case 30:
                return Typeface.create("kellyslab", Typeface.NORMAL);
            case 31:
                return Typeface.create("monad", Typeface.NORMAL);
            case 32:
                return Typeface.create("noir", Typeface.NORMAL);
            case 33:
                return Typeface.create("outrun-future", Typeface.NORMAL);
            case 34:
                return Typeface.create("pompiere", Typeface.NORMAL);
            case 35:
                return Typeface.create("reemkufi", Typeface.NORMAL);
            case 36:
                return Typeface.create("riviera", Typeface.NORMAL);
            case 37:
                return Typeface.create("the-outbox", Typeface.NORMAL);
            case 38:
                return Typeface.create("themeable-date", Typeface.NORMAL);
            case 39:
                return Typeface.create("vibur", Typeface.NORMAL);
            case 40:
                return Typeface.create("voltaire", Typeface.NORMAL);
        }
    }

}
