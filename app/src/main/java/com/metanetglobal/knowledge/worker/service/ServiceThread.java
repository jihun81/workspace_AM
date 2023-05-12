package com.metanetglobal.knowledge.worker.service;


import android.os.Handler;
import android.util.Log;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;
    MyService msv ;

    public ServiceThread(Handler handler) {
        this.handler = handler;
    }

    public ServiceThread(MyService myic) {
        this.msv = myic;
    }


    public void stopForever() {
        synchronized (this) {
            this.isRun = false;
        }
    }
    public void run() {
        //반복적으로 수행할 작업을 한다.
        while (isRun) {
            msv.NotiHeadUp();
            //handler.sendEmptyMessage( 0 );//쓰레드에 있는 핸들러에게 메세지를 보냄
            try {
                Thread.sleep( 5000 ); //10초씩 쉰다.
            } catch (Exception e) {
            }
        }
    }
}