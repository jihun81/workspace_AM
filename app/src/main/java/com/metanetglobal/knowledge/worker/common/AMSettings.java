package com.metanetglobal.knowledge.worker.common;

import com.metanetglobal.knowledge.worker.service.ServiceThread;

import java.util.HashSet;

/**
 * App 내부 설정 및 변수 모음
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class AMSettings {
    public static boolean isDebugMode = true;

    /**
     * 출퇴근 기능 사용여부
     *
     * false 시 fragment_main.xml 에 Layout 도 변경해주어야 한다.
     */
    public static boolean useOnOffWork = true;

    public static final String dev_baseUrl = "http://10.245.18.36:3000/";
    //public static final String real_baseUrl = "https://am.pulmuone.com/";
    public static final String baseUrl = dev_baseUrl;
    //public static final String baseUrl = dev_baseUrl;
    public static final String APP_DOWN_URL = baseUrl;

    public static final int RELOGIN_RETRY_COUNT = 3;

    public static final String PREFS_INFO = "AM_PREFS_INFO";
    public static final String SKEY = "P87Auh13vUHxF2H4Bs0spA==";

    public static String PHONE = "0000";
    public static String EMPNO = "00001";
    public static String STARTTIME = "090000";
    public static String ENDTIME = "180000";

    public static String workOutTimeChk = "off";
    public static String workInTimeChk = "on";

    /**
     * 세션 이름(헤더에 사용)
     */
    public static String SESSION_NAME = "Cookie";

    /**
     * Cookie
     */
    public static HashSet<String> COOKIESET = new HashSet<>();

    /**
     * Error Type
     */
    public class ErrorType {
        public static final int SESSION_ERROR = 200410;
    }
}