package com.metanetglobal.knowledge.worker.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.WindowManager;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BaseActivity;
import com.github.ajalt.timberkt.Timber;
import com.metanetglobal.knowledge.worker.main.otto_interfaces.Event_UpdateUserInfo;
import com.metanetglobal.knowledge.worker.service.MyService;
import com.squareup.otto.Subscribe;

/**
 * Main Activity
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseActivity
 */
public class MainActivity extends BaseActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private String userInfoString = "";
    private String workInOutString = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Set Status Bar Color to ColorPrimary
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_text_login_login_button_disabled));

        setContentView(R.layout.activity_base);

        Timber.tag(TAG).i("onCreate");

        // 앱 실행시 Background Service 실행
        Intent serviceintent = new Intent( MainActivity.this, MyService.class );
        startService( serviceintent );
        //stopService( serviceintent );


        /**
         * Login 이나 Intro 에서 전달받은 값으로 회원 정보 및 출퇴근 목록 을 MainFragment 에서 보여준다.
         */
        if(savedInstanceState != null) {
            userInfoString = savedInstanceState.getString("USER_INFO");
            workInOutString = savedInstanceState.getString("WORK_IN_OUT");
        } else {
            if(getIntent() != null) {
                userInfoString = getIntent().getStringExtra("USER_INFO");
                workInOutString = getIntent().getStringExtra("WORK_IN_OUT");
            }
        }

        /**
         * Replace to MainFragment
         */
        Fragment fragment = MainFragment.getInstance(userInfoString, workInOutString);
        replaceFragmentSafely(fragment, String.valueOf(0), R.id.layout_content_holder, false, false, 0, 0, 0, 0);
    }

    /**
     * 회원 정보 와 출퇴근 목록을 Bundle 에 저장해둔다.
     *
     * @param outState  Bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("USER_INFO", userInfoString);
        outState.putString("WORK_IN_OUT", workInOutString);

        super.onSaveInstanceState(outState);
    }

    /**
     * OTTO Event 발생시 회원정보를 업데이트 해준다.
     *
     * @param event Event of OTTO
     */
    @Subscribe
    public void getUpdateUserInfoEvent(Event_UpdateUserInfo event) {
        userInfoString = event.getUserInfo();
        workInOutString = event.getWorkInOutList();

        if(getCurrentFragment() != null && getCurrentFragment() instanceof MainFragment) {
            ((MainFragment) getCurrentFragment()).refreshUserInfo(userInfoString, workInOutString);
        }
    }
}

