package com.metanetglobal.knowledge.worker.schedule;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.WindowManager;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BaseActivity;
import com.github.ajalt.timberkt.Timber;
import com.metanetglobal.knowledge.worker.schedule.otto_interfaces.Event_UpdateSchedule;
import com.squareup.otto.Subscribe;

/**
 * Schedule List Activity
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseActivity
 */
public class ScheduleListActivity extends BaseActivity {
    private final String TAG = ScheduleListActivity.class.getSimpleName();

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

        /**
         * Replace to ScheduleListFragment
         */
        Fragment fragment = ScheduleListFragment.getInstance();
        replaceFragmentSafely(fragment, String.valueOf(0), R.id.layout_content_holder, false, false, 0, 0, 0, 0);
    }

    @Subscribe
    public void getUpdateScheduleEvent(Event_UpdateSchedule event) {
        if(getCurrentFragment() != null && getCurrentFragment() instanceof ScheduleListFragment) {
            ((ScheduleListFragment) getCurrentFragment()).refreshList();
        }
    }
}
