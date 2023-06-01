package com.metanetglobal.knowledge.worker;


import android.app.Application;
import android.app.Activity;

//import com.crashlytics.android.Crashlytics;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.github.ajalt.timberkt.Timber;

import java.util.LinkedList;

//import io.fabric.sdk.android.Fabric;

/**
 * Application Class of This App
 *
 * @author      namki.an
 * @version     1.0.0
 * @see         android.app.Application
 */
public class AMApplication extends Application {
    private static AMApplication singleton = null;

    private LinkedList<Activity> activityList = new LinkedList<>();

    public static AMApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Fabric.with(this, new Crashlytics());
        singleton = this;

        if(AMSettings.isDebugMode) {
            Timber.plant(Timber.DebugTree());
        }
    }

    /**
     * Add to Activity List
     *
     * @param activity      추가할 Activity
     */
    public void addActivityList(Activity activity) {
        try {
            if(activityList != null) {
                activityList.add(activity);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /**
     * Remove from Activity List
     *
     * @param activity      삭제할 Activity
     */
    public void removeActivityList(Activity activity) {
        try {
            if (activityList != null && activityList.size() > 0) {
                activityList.remove(activity);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}