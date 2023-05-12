package com.metanetglobal.knowledge.worker.common.ui;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BusProvider;
import com.metanetglobal.knowledge.worker.common.Utils;

/**
 * Normal Alert Dialog
 *
 * X 버튼과 단순 Message 로만 구성된 Dialog
 *
 * @author      namki.an
 * @version     1.0.0
 */
public class NormalAlertDialog extends DialogFragment {
    private final String TAG = NormalAlertDialog.class.getSimpleName();

    private ImageButton closeButton;
    private TextView messageTextView;
    private String message;
    private View contentView;

    private DialogInterface.OnDismissListener dismissListener;

    /**
     * Default Constructor
     *
     * @param message       Message of Dialog
     * @return              NormalAlertDialog
     */
    public static NormalAlertDialog newInstance(String message) {
        NormalAlertDialog dialog = new NormalAlertDialog();
        dialog.message = message;
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.getDialog() != null && this.getDialog().getWindow() != null) {
            this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            this.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        this.contentView = inflater.inflate(R.layout.dialog_normal_alert, container, false);
        this.closeButton = (ImageButton) contentView.findViewById(R.id.normal_alert_dialog_close_button);
        this.messageTextView = (TextView) contentView.findViewById(R.id.normal_alert_dialog_message);

        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });

        return this.contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getDialog() != null && dismissListener != null) {
            getDialog().setOnDismissListener(dismissListener);
        }

        BusProvider.getInstance().register(this);

        messageTextView.setText(Utils.nullCheck(message, getResources().getString(R.string.error_message_default_network_error)));
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * Dialog 가 Width, Height 설정을 안해주면 보이질 않는다...
         * 따라서 여기서 직접 설정해줬다.
         */
        Window window = getDialog().getWindow();
        if(window != null) {
            window.setLayout(getResources().getDimensionPixelSize(R.dimen.normal_alert_dialog_min_width), getResources().getDimensionPixelSize(R.dimen.normal_alert_dialog_min_height));
            window.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);

        super.onDestroyView();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        dismiss();
    }

    /**
     * Set Dismiss Listener
     *
     * @param dismissListener   DismissListener of dialog
     */
    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    /**
     * Post the event to the service bus
     *
     * @param event The event to dispatch on the service bus
     */
    public void postEvent(Object event) {
        BusProvider.getInstance().post(event);
    }
}
