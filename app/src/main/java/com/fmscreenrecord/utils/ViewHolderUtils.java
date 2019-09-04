package com.fmscreenrecord.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-6-4
 * Time: 下午3:28
 */
public class ViewHolderUtils {
    public static <T extends ViewHolder> T initViewHolder(View viewGroup, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        try {
            Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            T obj = c.newInstance();
            Context context = viewGroup.getContext();
            for (Field field : fields) {
                if (View.class.isAssignableFrom(field.getType())) {
                    try {
                        int rid = context.getResources().getIdentifier(field.getName(),
                                "id", context.getPackageName());
                        View view = viewGroup.findViewById(rid);
                        field.setAccessible(true);
                        field.set(obj, view);
                    } catch (Exception ex) {
                        Log.e("ViewHolderUtils.initViewHolder", "Filed:" + field.getName() + ",ErrMsg:" + ex.getMessage());
                        throw ex;
                    }
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
