package com.metanetglobal.knowledge.worker.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.ajalt.timberkt.Timber;

import static io.fabric.sdk.android.Fabric.TAG;

/**
 * SharedPreference Manager
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class PreferenceManager {
    /**
     * User ID 저장
     *
     * @param context   Context
     * @param id        To be saved User ID
     */
    public static void setUserId(Context context, String id) {
        try {
            SharedPreferences sp = context.getSharedPreferences(AMSettings.PREFS_INFO, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("USER_ID", Utils.AES_Encode(id, AMSettings.SKEY));
            Timber.tag(TAG).d("doAuthentication]"+id+"///"+ Utils.AES_Encode(id, AMSettings.SKEY));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * User ID 가져오기
     *
     * @param context   Context
     * @return          Saved User ID
     */
    public static String getUserId(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(AMSettings.PREFS_INFO, 0);
            String id = sp.getString("USER_ID", "");
            if(id != null && id.length() > 0) {
                return Utils.AES_Decode(id, AMSettings.SKEY);
            }

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * User PWD 저장
     *
     * @param context   Context
     * @param pwd       To be saved User Password
     */
    public static void setUserPwd(Context context, String pwd) {
        try {
            SharedPreferences sp = context.getSharedPreferences(AMSettings.PREFS_INFO, 0);
            SharedPreferences.Editor editor = sp.edit();
            Timber.tag(TAG).d("doAuthentication]"+pwd+"///");
            editor.putString("USER_PWD", Utils.AES_Encode(pwd, AMSettings.SKEY));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * User PWD 가져오기
     *
     * @param context   Context
     * @return          Saved User Password
     */
    public static String getUserPwd(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(AMSettings.PREFS_INFO, 0);
            String pwd = sp.getString("USER_PWD", "");
            if(pwd != null && pwd.length() > 0) {
                return Utils.AES_Decode(pwd, AMSettings.SKEY);
            }

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void setUserCode(Context context, String id) {
        try {
            SharedPreferences sp = context.getSharedPreferences(AMSettings.PREFS_INFO, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("USER_CODE", Utils.AES_Encode(id, AMSettings.SKEY));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * User ID 가져오기
     *
     * @param context   Context
     * @return          Saved User ID
     */
    public static String getUserCode(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(AMSettings.PREFS_INFO, 0);
            String id = sp.getString("USER_CODE", "");
            if(id != null && id.length() > 0) {
                return Utils.AES_Decode(id, AMSettings.SKEY);
            }

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
