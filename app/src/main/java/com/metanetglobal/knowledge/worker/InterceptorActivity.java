package com.metanetglobal.knowledge.worker;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.metanetglobal.knowledge.worker.common.BaseActivity;
import com.metanetglobal.knowledge.worker.intro.IntroActivity;

/**
 * 가장 처음에 실행되는 Activity
 * UI 는 없으며, Push 를 비롯한 최초 동작 Activity 를 정하기 위하여 생성하였다.
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         BaseActivity
 */
public class InterceptorActivity extends BaseActivity {
    private String type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 현재는 Push 를 받고 하는 부분이 없기 때문에 바로 Intro 로 보낸다.
        if(type.isEmpty()) {
            goIntro();
        }

        finish();
    }

    /**
     * Intro 로 이동
     */
    private void goIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}