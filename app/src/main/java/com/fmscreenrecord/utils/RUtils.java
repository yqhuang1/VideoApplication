package com.fmscreenrecord.utils;

import android.content.Context;

import com.fmscreenrecord.app.SRApplication;

import java.util.HashMap;

/**
 * User: Ryan
 * Date: 11-10-12
 * Time: Afternoon 2:30
 */
public class RUtils {
    private static Class rStyleableClz = null;
    private static Class rStyleClz = null;
    private static final HashMap<Integer, Integer> maps = new HashMap<Integer, Integer>();
    private static Context mContent;

    static {
        mContent = SRApplication.Get();
    }
    
    private static Class getRClass(Context context, String name) {
        try {
            return Class.forName(context.getPackageName() + ".R$" + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class getRStyleableClz(Context context) {
        if (null == rStyleableClz) {
            rStyleableClz = getRClass(context, "styleable");
        }
        return rStyleableClz;
    }

    private static Class getRStyleClz(Context context) {
        if (null == rStyleClz) {
            rStyleClz = getRClass(context, "style");
        }
        return rStyleClz;
    }

    /**
     * @param name name
     * @param type type
     * @return int The associated resource identifier.  Returns 0 if no such
     *         resource was found.  (0 is not a valid resource ID.)
     */
    public static int getIdentifier(String name, String type) {
        Integer keyValue = (type + "_" + name).hashCode();
        Integer res = maps.get(keyValue);
        if (null != res) {
            return res;
        } else {
            Context context = mContent;
            try {
                if ("styleable".equals(type)) {
                    res = (Integer) getRStyleableClz(context).getDeclaredField(name).get("");
                } else if ("style".equals(type)) {
                    res = (Integer) getRStyleClz(context).getDeclaredField(name).get("");
                } else {
                    res = context.getResources().getIdentifier(name, type, context.getPackageName());
                }
            } catch (Exception e) {
                res = 0;
            }
            if (res > -1) {
                maps.put(keyValue, res);
                return res;
            } else {
                throw new RuntimeException("res id not found in project, type :" + type + " name :" + name);
            }
        }
    }
 

    public static int getRStringID(String name) {
        return getIdentifier(name, "string");
    }

    public static String getRString(String name) {
        return mContent.getString(getRStringID(name));
    }

    public static int getRLayoutID(String name) {
        return getIdentifier(name, "layout");
    }

    public static int getRID(String name) {
        return getIdentifier(name, "id");
    }

    public static int getRRawID(String name) {
        return getIdentifier(name, "raw");
    }

    public static int[] getRStyleableIDs(String name) {
        try {
            return (int[]) getRStyleableClz(mContent).getDeclaredField(name).get("");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new int[]{0};
    }

    public static int getRStyleableID(String name) {
        return getIdentifier(name, "styleable");
    }

    public static int getRDrawableID(String name) {
        return getIdentifier(name, "drawable");
    }

    public static int getRXmlID(String name) {
        return getIdentifier(name, "xml");
    }

    public static int getRAnimID(String name) {
        return getIdentifier(name, "anim");
    }

    public static int getRMenuID(String name) {
        return getIdentifier(name, "menu");
    }

    public static int getRArrayID(String name) {
        return getIdentifier(name, "array");
    }

    public static int getRColorID(String name) {
        return getIdentifier(name, "color");
    }

    public static int getRStyleID(String name) {
        return getIdentifier(name, "style");
    }
    
    public static int getRDimenID(String name) {
        return getIdentifier(name, "dimen");
    }
}
