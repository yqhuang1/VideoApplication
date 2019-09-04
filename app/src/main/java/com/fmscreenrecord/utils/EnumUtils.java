package com.fmscreenrecord.utils;

import java.util.EnumSet;

public class EnumUtils {
    public static <T extends Enum> T ordinalOf(Class<T> clazz, int ordinal) {
        T t = null;
        EnumSet enumSet = EnumSet.allOf(clazz);
        for (Object object : enumSet) {
            if (object instanceof Enum) {
                Enum e = (Enum) object;
                if (e.ordinal() == ordinal) {
                    //noinspection unchecked
                    t = (T) e;
                    break;
                }
            }
        }
        return t;
    }
}