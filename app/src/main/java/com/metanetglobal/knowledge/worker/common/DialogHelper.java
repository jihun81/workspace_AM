package com.metanetglobal.knowledge.worker.common;

import android.content.DialogInterface;


import androidx.fragment.app.FragmentActivity;

import android.app.DialogFragment;

import com.metanetglobal.knowledge.worker.common.ui.NormalAlertDialog;

/**
 * Dialog Helper
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class DialogHelper {
    /**
     * Show Normal Alert Dialog
     *
     * @param activity  Activity
     * @param message   Message of Dialog
     */
    public static void showNormalAlertDialog(FragmentActivity activity, String message) {
        showNormalAlertDialog(activity, message, null);
    }

    /**
     * Show Normal Alert Dialog With dismissListener
     *
     * @param activity  Activity
     * @param message   Message of Dialog
     * @param dismissListener   DismissListener of Dialog
     */
    public static void showNormalAlertDialog(FragmentActivity activity, String message, DialogInterface.OnDismissListener dismissListener) {
        if(activity == null || activity.isFinishing()) {
            return;
        }

        try {
            NormalAlertDialog newFragment = NormalAlertDialog.newInstance(message);
            if(dismissListener != null) {
                ((NormalAlertDialog)newFragment).setDismissListener(dismissListener);
            }
            newFragment.show(activity.getSupportFragmentManager(), "NormalAlertDialog");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
