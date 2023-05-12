package com.metanetglobal.knowledge.worker.schedule;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.WindowManager;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BaseActivity;
import com.github.ajalt.timberkt.Timber;

/**
 * Schedule Detail Activity
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseActivity
 */
public class ScheduleDetailActivity extends BaseActivity {
    private final String TAG = ScheduleDetailActivity.class.getSimpleName();

    private String scheduleId = "";
    private int mode = ScheduleDetailFragment.MODE_ADD;
    private String mode_date = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Set Status Bar Color to ColorPrimary
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        setContentView(R.layout.activity_base);

        Timber.tag(TAG).i("onCreate");

        postponeEnterTransition();

        if(savedInstanceState != null) {
            scheduleId = savedInstanceState.getString("SCHEDULE_ID");
            mode = savedInstanceState.getInt("MODE");
        } else {
            if(getIntent() != null) {
                scheduleId = getIntent().getStringExtra("SCHEDULE_ID");
                mode = getIntent().getIntExtra("MODE", ScheduleDetailFragment.MODE_ADD);
                mode_date = getIntent().getStringExtra("SCHEDULE_DATE");
            }
        }

        /**
         * Replace to ScheduleDetailFragment
         */
        Fragment fragment = ScheduleDetailFragment.getInstance(mode, scheduleId,mode_date);
        replaceFragmentSafely(fragment, String.valueOf(0), R.id.layout_content_holder, false, false, 0, 0, 0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("SCHEDULE_ID", scheduleId);
        outState.putInt("MODE", mode);

        super.onSaveInstanceState(outState);
    }
}
