package com.metanetglobal.knowledge.worker.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.AnimRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.metanetglobal.knowledge.worker.AMApplication;
import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.auth.LoginActivity;
import com.metanetglobal.knowledge.worker.auth.LoginFragment;
import com.metanetglobal.knowledge.worker.common.ui.LoadingProgressDialog;
import com.metanetglobal.knowledge.worker.main.MainActivity;
import com.metanetglobal.knowledge.worker.main.MainFragment;

/**
 * Base Activity
 *
 * 모든 Activity 는 이 Class 를 상속 받는다.
 *
 * @author      namki.an
 * @version     1.0.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    /** Loading Progress Dialog */
    private LoadingProgressDialog loadingProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AMApplication.getInstance().addActivityList(this);

        //register the activity to the service bus
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        AMApplication.getInstance().removeActivityList(this);

        hideLoadingProgressBar();

        BusProvider.getInstance().unregister(this);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            /**
             * Back Key 누를 시 앱 종료 팝업 발생
             */
            if(((this instanceof LoginActivity && getCurrentFragment() != null && getCurrentFragment() instanceof LoginFragment)) ||
                (this instanceof MainActivity && getCurrentFragment() != null && getCurrentFragment() instanceof MainFragment)) {
                new AlertDialog.Builder(this)
                        .setMessage(getResources().getString(R.string.exit_popup_message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
                return;
            }

            if(getFragmentCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

    /**
     * Loading ProgressBar 공통
     *
     * @param cancelable ProgressBar Cancel 가능 여부
     */
    public void showLoadingProgressBar(final boolean cancelable) {
        if(loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
            return;
        }

        try {
            if(isFinishing()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingProgressDialog = new LoadingProgressDialog(BaseActivity.this, cancelable);
                    loadingProgressDialog.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading ProgressBar 닫는다.
     */
    public void hideLoadingProgressBar() {
        try {
            if(loadingProgressDialog != null && loadingProgressDialog.isShowing()) {
                loadingProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Replace Fragment Safely
     *
     * @param fragment              Fragment
     * @param TAG                   TAG
     * @param containerViewId       View id
     * @param addToBackStack        Whether of add to back stack
     * @param allowStateLoss        Whether of allow state loss
     * @param enterAnimation        Animation of Enter
     * @param exitAnimation         Animation of Exit
     * @param popEnterAnimation     Animation of pop enter
     * @param popExitAnimation      Animation of pop exit
     */
    public void replaceFragmentSafely(Fragment fragment,
                                      String TAG,
                                      @IdRes int containerViewId,
                                      boolean addToBackStack,
                                      boolean allowStateLoss,
                                      @AnimRes int enterAnimation,
                                      @AnimRes int exitAnimation,
                                      @AnimRes int popEnterAnimation,
                                      @AnimRes int popExitAnimation) {
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
                .replace(containerViewId, fragment, TAG);
        if(addToBackStack) {
            ft.addToBackStack(null);
        }
        if(!getSupportFragmentManager().isStateSaved()) {
            ft.commit();
        } else if (allowStateLoss) {
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * Add Fragment Safely
     *
     * @param fragment              Fragment
     * @param allowStateLoss        Whether of allow state loss
     * @param containerViewId       View id
     * @param enterAnimation        Animation of Enter
     * @param exitAnimation         Animation of Exit
     * @param popEnterAnimation     Animation of pop enter
     * @param popExitAnimation      Animation of pop exit
     */
    public void addFragmentSafely(Fragment fragment,
                                            boolean allowStateLoss,
                                            @IdRes int containerViewId,
                                            @AnimRes int enterAnimation,
                                            @AnimRes int exitAnimation,
                                            @AnimRes int popEnterAnimation,
                                            @AnimRes int popExitAnimation) {
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)
                .add(containerViewId, fragment, String.valueOf(getFragmentCount() + 1))
                .addToBackStack(null);
        if(!getSupportFragmentManager().isStateSaved()) {
            ft.commit();
        } else if (allowStateLoss) {
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * Find fragment by IdRes.
     *
     * @param frameId ID of fragment
     */
    public Fragment getContentFragment(@IdRes int frameId) {
        return getSupportFragmentManager().findFragmentById(frameId);
    }

    /**
     * Find Fragment by Tag.
     *
     * @param tag TAG of fragment
     */
    public Fragment getContentFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * index 의 Fragment 가져오기
     *
     * @param index index of fragment
     * @return fragment
     */
    public Fragment getFragmentAt(int index) {
        return getSupportFragmentManager().findFragmentByTag(String.valueOf(index));
    }

    /**
     * Get Fragment Count
     */
    public int getFragmentCount() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    /**
     * Current Fragment 가져오기
     *
     * @return Current Fragment
     */
    public Fragment getCurrentFragment() {
        return getFragmentAt(getFragmentCount());
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