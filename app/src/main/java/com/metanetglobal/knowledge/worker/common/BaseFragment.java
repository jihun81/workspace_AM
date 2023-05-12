package com.metanetglobal.knowledge.worker.common;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.metanetglobal.knowledge.worker.auth.LoginActivity;

import java.lang.reflect.Field;

/**
 * Base Fragment
 *
 * 모든 Fragment 는 이 Class 를 상속 받는다.
 *
 * @author      namki.an
 * @version     1.0.0
 */
public abstract class BaseFragment extends Fragment {
    // Session (200410) Error 발생 후 Retry Count
    public int relogin_retry_count = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    /**
     * Get Root Layout
     *
     * @return Layout Resource of Root
     */
    @LayoutRes
    public abstract int getLayout();

    @Override
    public void onStart() {
        super.onStart();

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        /**
         * 앱이 비정상적으로 종료되는 현상 방지
         * (Reset Child FragmentManager)
         */
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Post the event to the service bus
     *
     * @param event The event to dispatch on the service bus
     */
    public void postEvent(Object event) {
        BusProvider.getInstance().post(event);
    }

    /**
     * Show Loading ProgressBar
     */
    public void showLoadingProgressBar() {
        if(getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).showLoadingProgressBar(true);
        }
    }

    /**
     * Show Loading ProgressBar
     *
     * @param cancelable    cancelable
     */
    public void showLoadingProgressBar(boolean cancelable) {
        if(getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).showLoadingProgressBar(cancelable);
        }
    }

    /**
     * Hide Loading ProgressBar
     */
    public void hideLoadingProgressBar() {
        if(getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity)getActivity()).hideLoadingProgressBar();
        }
    }

    /**
     * Go to LoginActivity
     *
     * All Activities will be cleared
     */
    public void goLogin() {
        if(getActivity() != null) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Login
     *
     * @param requestCode   RequestCode of Login
     * @param stringParam   Param of Login
     */
    public abstract void doAuthentication(int requestCode, String stringParam);

    /**
     * Set ReLogin Count
     *
     * @param count Count of ReLogin
     */
    public void setReLogInCount(int count) {
        relogin_retry_count = count;
    }

    /**
     * Get ReLogin Count
     *
     * @return  ReLogin Count
     */
    public int getReLogInCount() {
        return relogin_retry_count;
    }
}