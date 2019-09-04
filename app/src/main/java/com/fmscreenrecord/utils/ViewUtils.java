package com.fmscreenrecord.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import com.fmscreenrecord.app.SRApplication;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Ryan
 * Date: 11-9-18
 * Time: A.M. 10:37
 */
public class ViewUtils {
    public static int GetResourcesColor(Resources res, int cId) {
        return res.getColor(cId);
    }

    public static float ConvertDimension(Resources res, String st) {
        st = st.toLowerCase();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float value = ConvertDimensionPixel(st, metrics, "px", TypedValue.COMPLEX_UNIT_PX);
        if (value > -1) return value;
        value = ConvertDimensionPixel(st, metrics, "dip", TypedValue.COMPLEX_UNIT_DIP);
        if (value > -1) return value;
        value = ConvertDimensionPixel(st, metrics, "sp", TypedValue.COMPLEX_UNIT_SP);
        if (value > -1) return value;
        value = ConvertDimensionPixel(st, metrics, "pt", TypedValue.COMPLEX_UNIT_PT);
        if (value > -1) return value;
        value = ConvertDimensionPixel(st, metrics, "in", TypedValue.COMPLEX_UNIT_IN);
        if (value > -1) return value;
        value = ConvertDimensionPixel(st, metrics, "mm", TypedValue.COMPLEX_UNIT_MM);
        if (value > -1) return value;
        return 0;
    }

    public static float ConvertDimensionPixel(String st, DisplayMetrics metrics, String unitStr, int unitInt) {
        if (st.endsWith(unitStr)) {
            float value = Float.valueOf(st.substring(0, st.length() - unitStr.length()));
            return TypedValue.applyDimension(unitInt, value, metrics);
        }
        return -1;
    }

    public static void RemoveParent(View view) {
        ViewParent vp = view.getParent();
        if (null != vp) {
            ((ViewGroup) vp).removeView(view);
        }
    }

    public static void toastString(String msg) {
        Toast.makeText(SRApplication.Get(), msg, Toast.LENGTH_LONG).show();
    }


    public static Drawable getDrawable(int resId) {
        return SRApplication.Get().getResources().getDrawable(resId);
    }

    public static Drawable createSelector(int normalResId, int selectedResId, int pressedResId) {
        final Drawable normal = 0 == normalResId ? null : getDrawable(normalResId);
        final Drawable selected = 0 == selectedResId ? null : getDrawable(selectedResId);
        final Drawable pressed = 0 == pressedResId ? null : getDrawable(pressedResId);
        return new Selector(SRApplication.Get()).setState(normal, selected, pressed);
    }

    public static void setVisibility(View view, int visible) {
        if (null == view) return;
        if (visible != view.getVisibility()) {
            view.setVisibility(visible);
        }
    }

    public static boolean isVisible(View view) {
        return null != view && View.VISIBLE == view.getVisibility();
    }


    static class Selector extends View {
        public Selector(Context context) {
            super(context);
        }

        // 以下这个方法也可以把你的图片数组传过来，以StateListDrawable来设置图片状态，来表现button的各中状态。未选
        // 中，按下，选中效果。
        public StateListDrawable setState(Drawable normal, Drawable selected, Drawable pressed) {
            StateListDrawable drawable = new StateListDrawable();
            if (null != pressed) {
                drawable.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
            }
            if (null != selected) {
                drawable.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
                drawable.addState(View.FOCUSED_STATE_SET, selected);
            }
            if (null != normal) {
                drawable.addState(View.ENABLED_STATE_SET, normal);
                drawable.addState(View.EMPTY_STATE_SET, normal);
            }
            return drawable;
        }
    }

    public static void setAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT > 10) {
            view.setAlpha(alpha);
        } else {
           /* AlphaAnimation anim = new AlphaAnimation(alpha, alpha);
            anim.setDuration(0l); // Make animation instant
            anim.setFillAfter(true); // Tell it to persist after the animation ends
            view.startAnimation(anim);*/
        }
    }

    public static void setDialogShowing(Dialog dialog, boolean mShowing) {
        try {
            Field field = dialog.getClass()
                    .getSuperclass().getDeclaredField(
                            "mShowing");
            field.setAccessible(true);
            field.set(dialog, mShowing);
        } catch (Exception e) {
            //
        }
    }

    public static void hideStatusPanel(Context context) {
        try {
            Object o = context.getSystemService("statusbar");
            Class<?> c = Class.forName("android.app.StatusBarManager");
            Method m = c.getMethod("collapse");
            m.invoke(o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}