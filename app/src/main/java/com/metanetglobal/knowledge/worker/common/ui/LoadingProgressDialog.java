package com.metanetglobal.knowledge.worker.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.metanetglobal.knowledge.worker.R;

/**
 * Loading Progress Dialog
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class LoadingProgressDialog extends Dialog {
    /**
     * Constructor of LoadingProgressDialog
     *
     * @param context   context
     */
    public LoadingProgressDialog(Context context) {
        this(context, true);
    }

    /**
     * Constructor of LoadingProgressDialog
     *
     * @param context   context
     * @param cancelable    cancelable
     */
    public LoadingProgressDialog(Context context, boolean cancelable) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        setContentView(R.layout.dialog_loading_progress);

        setCancelable(cancelable);
    }
}
