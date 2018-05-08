package pi.com.pi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 描述：App共享属性通用属性
 * 作者： Sunny
 * 日期： 2015-09-17 15:29
 * 版本： v2.7.2
 */
public class SpUtil {

    /**
     * 未识别的Key.
     */
    public static final int KEY_NOT_FOUND = -1;
    public static final int INT_KEY_NOT_FOUND = -99999;
    public static final long LONg_KEY_NOT_FOUND = 0;
    /**
     * 未识别的Key.
     */
    public static final String STRING_KEY_NOT_FOUND = null;

    /**
     * 未识别的Key.
     */
    public static final boolean BOOLEAN_KEY_NOT_FOUND = false;

    public static final String APP_PREFERENCE = "app";

    /**
     * 私有的构造方法
     */
    private SpUtil() {
    }

    /**
     * 获取App SharePreferences实例.
     *
     * @param context 上下文信使
     * @return AppDelegate SharePreferences实例
     */
    private static SharedPreferences getAppPreferences(final Context context) {
        SharedPreferences appPreferences = context.getSharedPreferences(
                APP_PREFERENCE, Context.MODE_PRIVATE);
        return appPreferences;
    }

    /**
     * 移出App SharePreferences中的键
     *
     * @param context 上下文信
     * @param strKey
     */
    public static void removeKey(final Context context, final String strKey) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        Editor editor = appPreferences.edit();
        editor.remove(strKey);
        editor.commit();
    }

    /**
     * 移出App SharePreferences中所有的键
     *
     * @param context 上下文信
     */
    public static void clear(final Context context) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        Editor editor = appPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 将键值对保存到App SharePreferences
     *
     * @param context 上下文信
     * @param strKey
     * @param iValue
     */
    public static void putInt(final Context context, final String strKey,
                              final int iValue) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        Editor editor = appPreferences.edit();
        editor.putInt(strKey, iValue);
        editor.commit();
    }

    public static void putLong(final Context context, final String strKey,
                               final long iValue) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        Editor editor = appPreferences.edit();
        editor.putLong(strKey, iValue);
        editor.commit();
    }


    /**
     * 将键值对保存到App SharePreferences
     *
     * @param context 上下文信
     * @param strKey
     * @param bFlag
     */
    public static void putBoolean(final Context context, final String strKey,
                                  final boolean bFlag) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        Editor editor = appPreferences.edit();
        editor.putBoolean(strKey, bFlag);
        editor.commit();
    }

    /**
     * 将键值对保存到App SharePreferences
     *
     * @param context  上下文
     * @param strKey
     * @param strValue
     */
    public static void putString(final Context context, final String strKey,
                                 final String strValue) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        Editor editor = appPreferences.edit();
        editor.putString(strKey, strValue);
        editor.commit();
    }

    /**
     * 从App SharePreferences中读取键对应的
     *
     * @param context 上下文信
     * @param strKey
     * @return AppDelegate SharePreferences中读取键对应的，未找到返回
     * {@link SpUtil#INT_KEY_NOT_FOUND}
     */
    public static int getInt(final Context context, final String strKey) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        return appPreferences.getInt(strKey,
                SpUtil.INT_KEY_NOT_FOUND);
    }

    public static long getLong(final Context context, final String strKey) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        return appPreferences.getLong(strKey,
                SpUtil.LONg_KEY_NOT_FOUND);
    }

    /**
     * 从App SharePreferences中读取键对应的
     *
     * @param context 上下文信
     * @param strKey
     * @return AppDelegate SharePreferences中读取键对应的，未找到返回
     * {@link SpUtil#BOOLEAN_KEY_NOT_FOUND}
     */
    public static boolean getBoolean(final Context context, final String strKey) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        return appPreferences.getBoolean(strKey,
                SpUtil.BOOLEAN_KEY_NOT_FOUND);
    }

    /**
     * 从App SharePreferences中读取键对应的
     *
     * @param context 上下文信
     * @param strKey
     * @return AppDelegate SharePreferences中读取键对应的，未找到返回
     * {@link SpUtil#STRING_KEY_NOT_FOUND}
     */
    public static String getString(final Context context, final String strKey) {
        SharedPreferences appPreferences = SpUtil
                .getAppPreferences(context);
        return appPreferences.getString(strKey,
                SpUtil.STRING_KEY_NOT_FOUND);
    }


    public static void setIsNewUser(final Context context) {
        SpUtil.putBoolean(context, "newuser", true);
    }

    public static boolean isNewUser(final Context context) {
        return SpUtil.getBoolean(context, "newuser");
    }
}
