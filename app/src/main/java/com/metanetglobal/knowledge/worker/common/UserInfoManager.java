package com.metanetglobal.knowledge.worker.common;

import android.content.Context;

import com.github.ajalt.timberkt.Timber;

/**
 * User Info Manager
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class UserInfoManager {
    /**
     * 로그인 했는지 여부
     *
     * Preference 에 ID 와 Pwd 가 존재하면 로그인 했다고 치부한다.
     *
     * @param context   Context
     * @return          Is Logined
     */
    public static boolean isLogined(Context context) {
        Timber.tag("isLogined").d(PreferenceManager.getUserId(context).length()+"/"+PreferenceManager.getUserPwd(context));
        return PreferenceManager.getUserId(context).length() > 0 && PreferenceManager.getUserPwd(context).length() > 0;
    }
}
