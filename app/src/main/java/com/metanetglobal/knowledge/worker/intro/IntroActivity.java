package com.metanetglobal.knowledge.worker.intro;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BaseActivity;
import com.github.ajalt.timberkt.Timber;
import com.metanetglobal.knowledge.worker.service.MyService;

/**
 * Intro Activity
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseActivity
 */
public class IntroActivity extends BaseActivity {
    private final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        Timber.tag(TAG).i("onCreate");



        /**
         * Replace to IntroFragment
         */
        Fragment fragment = IntroFragment.getInstance();
        replaceFragmentSafely(fragment, String.valueOf(0), R.id.layout_content_holder, false, false, 0, 0, 0, 0);
    }
}