package com.metanetglobal.knowledge.worker.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.WindowManager;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BaseActivity;
import com.github.ajalt.timberkt.Timber;
import com.metanetglobal.knowledge.worker.service.MyService;

/**
 * Login Activity
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseActivity
 */
public class LoginActivity extends BaseActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Set Status Bar Color to White
         */
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(LoginActivity.this, android.R.color.white));
        }

        setContentView(R.layout.activity_base);

        Timber.tag(TAG).i("onCreate");

        // Service 해제
      //  Intent stopIntent = new Intent(LoginActivity.this, MyService.class);
      //  stopService(stopIntent);

        /**
         * Replace to LoginFragment
         */
        Fragment fragment = LoginFragment.getInstance();
        replaceFragmentSafely(fragment, String.valueOf(0), R.id.layout_content_holder, false, false, 0, 0, 0, 0);
    }
}
