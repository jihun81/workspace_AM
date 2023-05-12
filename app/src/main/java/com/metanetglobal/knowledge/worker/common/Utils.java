package com.metanetglobal.knowledge.worker.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.metanetglobal.knowledge.worker.main.MainActivity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utils Class
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class Utils {
    /**
     * String 의 Null 체크이다.
     *
     * @param str 체크하고자 하는 문자열
     * @return null 일경우 "" 리턴, 아닐경우 문자열 리턴
     */
    public static String nullCheck(String str) {
        if (str == null || str.trim().equals("")) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * String 의 Null 체크이다.
     *
     * @param str 체크하고자 하는 문자열
     * @param defaultString 디폴트 문자열
     * @return null 일경우 "" 리턴, 아닐경우 문자열 리턴
     */
    public static String nullCheck(String str, String defaultString) {
        if (str == null || str.trim().equals("")) {
            return defaultString;
        } else {
            return str;
        }
    }

    /**
     * 해당객체를 JSON 형태로 변환한다.
     *
     * @param obj 변환하고자하는 Object
     * @return JSON 형태로 변환된 문자열
     */
    public static String convertObjToJSON(Object obj) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(obj);
            return json;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 해당 JSON String 을 Object 형태로 변환한다.
     *
     * @param jsonString JSON String
     * @return 변환된 Object
     */
    public static Object convertJSONToObj(String jsonString) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<Object>(){}.getType();
            return gson.fromJson(jsonString, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get Screen Height
     *
     * @param activity  Activity
     * @return          Screen Height of Phone
     */
    public static int getScreenHeight(Activity activity) {
        if(activity != null && !activity.isFinishing()) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;

            return height;
        } else {
            return 0;
        }
    }

    /**
     * Get NavigationBar Height
     *
     * @param context   Context
     * @return          Height of NavigationBar
     */
    public static int getNavigationBarHeight(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y).y;
        }

        // navigation bar is not present
        return new Point().y;
    }

    /**
     * Get App Usable Screen Size
     *
     * @param context   Context
     * @return          Usable Screen Size
     */
    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Get Real Screen Size
     *
     * @param context   Context
     * @return          Real Screen Size
     */
    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        return size;
    }

    /**
     * SHA1 암호화
     *
     * @param text  To be encrypted string
     * @return      Encrypted string
     */
    public static String encryptSha1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] textBytes = text.getBytes("iso-8859-1");
            md.update(textBytes, 0, textBytes.length);
            byte[] sha1hash = md.digest();
            return convertToHex(sha1hash).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
        }

        return "";
    }

    /**
     * Byte[] to Hex
     *
     * @param data  Data to be converted to hex
     * @return      Hex String
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * AES Encode 암호화
     *
     * @param str 문자열
     * @param key Key
     * @return 암호화된 String
     */
    public static String AES_Encode(String str, String key) {
        byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        try {
            byte[] textBytes = str.getBytes("UTF-8");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
            return Base64.encodeToString(cipher.doFinal(textBytes), 0);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * AES Decode 복호화
     *
     * @param text 문자열
     * @param key Key
     * @return 복호화된 String
     */
    public static String AES_Decode(String text, String key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] keyBytes = new byte[16];
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] results = cipher.doFinal(Base64.decode(text, Base64.NO_WRAP));

            return new String(results, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Version 비교 Class
     *
     * compare(Version1, Version2) 로 비교
     */
    public static class CompareVersion {
        public CompareVersion() {
        }

        public static int compare(String version1, String version2) {
            String s1 = normalisedVersion(version1);
            String s2 = normalisedVersion(version2);

            // compare 된 값이 < 0 이면 s2 가 더 큼
            // compare 된 값이 > 0 이면 s1 이 더 큼
            // compare 된 값이 == 0 이면 같음
            return s1.compareTo(s2);
        }

        private static String normalisedVersion(String version) {
            return normalisedVersion(version, ".", 4);
        }

        private static String normalisedVersion(String version, String sep, int maxWidth) {
            String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
            StringBuilder sb = new StringBuilder();
            for(String s : split) {
                sb.append(String.format("%" + maxWidth + 's', s));
            }
            return sb.toString();
        }
    }


    public static String getPhoneNumber(Context context,Activity act) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        )
        {

            ActivityCompat.requestPermissions(act, new String[]{

                    Manifest.permission.READ_PHONE_NUMBERS
                    ,Manifest.permission.READ_PHONE_STATE
                    ,Manifest.permission.POST_NOTIFICATIONS

            }, 100);

        }
        String phoneNo = telManager.getLine1Number();

        if (nullToString(phoneNo,"").length()<=0) {
            phoneNo = "0000000000";
        }
        if (phoneNo.startsWith("+82")) {

            phoneNo = phoneNo.replace("+82", "0");
        }
        return phoneNo;
    }

    public static String nullToString(String value, String changeMsg) {
        return (value == null || "null".equals(value.toLowerCase())) ? changeMsg : value;
    }

}
