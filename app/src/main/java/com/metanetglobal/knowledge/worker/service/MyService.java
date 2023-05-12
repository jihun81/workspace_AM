package com.metanetglobal.knowledge.worker.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.AMSettings;
import com.metanetglobal.knowledge.worker.intro.IntroActivity;
import com.metanetglobal.knowledge.worker.main.MainActivity;
import com.metanetglobal.knowledge.worker.main.MainFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;




public class MyService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        myServiceHandler handler = new myServiceHandler();
        Log.d("test","~~~~~~~~~~01~~~~~~~~");

     //   thread = new ServiceThread(this);
     //   thread.start();
/*        try{
            Log.d("test",THREAD.isAlive()+"~~~~~~~~~~03~~~~~~~~"+THREAD.isRun);
        }catch (Exception e){
            thread.start();
        }*/
        //thread.stopForever();

        //THREAD = thread;

        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        myServiceHandler handler = new myServiceHandler();
        Log.d("test","~~~~~~~~~~02~~~~~~~~");
        //thread.stopForever();
        thread = new ServiceThread( this );
        thread.start();
    }

    public void start() {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread( this );
        thread.start();
    }

    public void stop() {
        myServiceHandler handler = new myServiceHandler();
        Log.d("test","~~~~~~~~~~03~~~~~~~~");
     //   thread = new ServiceThread( this );
        thread.stopForever();
    }

    public String test(){


        Calendar cal = Calendar.getInstance();
        int hour = cal.get( Calendar.HOUR_OF_DAY );
        int MIN = cal.get( Calendar.MINUTE );
        int SEC = cal.get( Calendar.SECOND );

        Log.d("test",hour+":"+MIN+":"+SEC);
        Date today = new Date();
        SimpleDateFormat tdf = new SimpleDateFormat("HHmmss");
        String tdtime  = tdf.format(today);

        if(Integer.parseInt(tdtime) > 101500){

            //알림(Notification)을 관리하는 관리자 객체를 운영체제(Context)로부터 소환하기
            NotificationManager notificationManager=(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
            NotificationCompat.Builder builder= null;

            Intent activityIntent = new Intent(this, MainFragment.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_MUTABLE);

            //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                String channelID="channel_01"; //알림채널 식별자
                String channelName="MyChannel01"; //알림채널의 이름(별명)

                //알림채널 객체 만들기
                NotificationChannel channel= new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_DEFAULT);

                //알림매니저에게 채널 객체의 생성을 요청
                notificationManager.createNotificationChannel(channel);

                //알림건축가 객체 생성
                builder=new NotificationCompat.Builder(this, channelID);


            }else{
                //알림 건축가 객체 생성
                builder= new NotificationCompat.Builder(this, null);
            }

            //건축가에게 원하는 알림의 설정작업
            builder.setSmallIcon(R.drawable.ic_logo);

            //상태바를 드래그하여 아래로 내리면 보이는
            //알림창(확장 상태바)의 설정
            builder.setContentTitle("퇴근확인");//알림창 제목
            builder.setContentText("퇴근 체크 해주세요.");//알림창 내용
            builder.setContentIntent(pendingIntent);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            //알림창의 큰 이미지
            Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher2_foreground);
            builder.setLargeIcon(bm);//매개변수가 Bitmap을 줘야한다.

            //건축가에게 알림 객체 생성하도록
            Notification notification=builder.build();

            //알림매니저에게 알림(Notify) 요청
            notificationManager.notify(1, notification);

            thread.stopForever();

        }else{

        }
        //알림 요청시에 사용한 번호를 알림제거 할 수 있음.
        return "ss";

    }


    public String NotiHeadUp(){ //알림 상단표시


        Calendar cal = Calendar.getInstance();
        int hour = cal.get( Calendar.HOUR_OF_DAY );
        int MIN = cal.get( Calendar.MINUTE );
        int SEC = cal.get( Calendar.SECOND );

        Log.d("test",hour+":"+MIN+":"+SEC);
        Date today = new Date();
        SimpleDateFormat tdf = new SimpleDateFormat("HHmmss");
        String tdtime  = tdf.format(today);
        Log.d("test",Integer.parseInt(AMSettings.ENDTIME)+"/"+AMSettings.workOutTimeChk+"/"+tdtime);

        if(Integer.parseInt(tdtime) > Integer.parseInt(AMSettings.ENDTIME)&&AMSettings.workOutTimeChk.equals("off")){

            //알림(Notification)을 관리하는 관리자 객체를 운영체제(Context)로부터 소환하기
            NotificationManager notificationManager=(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
            NotificationCompat.Builder builder= null;

            Intent activityIntent = new Intent(this, IntroActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Intent activityIntent2 = new Intent(this, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT); //FLAG_UPDATE_CURRENT 안드로이드 12버전 까지
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_MUTABLE); // FLAG_MUTABLE 안드로이드 13버전

            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, activityIntent2,  PendingIntent.FLAG_MUTABLE);

            //PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);    // 2
            //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                String channelID="channel_01"; //알림채널 식별자
                String channelName="MyChannel01"; //알림채널의 이름(별명)
                Log.d("test2",hour+":"+MIN+":"+SEC);
                //알림채널 객체 만들기
                NotificationChannel channel= new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_HIGH);

                //알림매니저에게 채널 객체의 생성을 요청
                notificationManager.createNotificationChannel(channel);

                //알림건축가 객체 생성
                builder=new NotificationCompat.Builder(this, channelID);


            }else{
                //알림 건축가 객체 생성
                builder= new NotificationCompat.Builder(this, null);
            }

            //건축가에게 원하는 알림의 설정작업
            builder.setSmallIcon(R.drawable.ic_logo);

            //상태바를 드래그하여 아래로 내리면 보이는
            //알림창(확장 상태바)의 설정
            builder.setContentTitle("퇴근확인");//알림창 제목
            builder.setContentText("퇴근 체크 해주세요.");//알림창 내용
            builder.setContentIntent(pendingIntent);
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setAutoCancel(true);
            builder.setFullScreenIntent(fullScreenPendingIntent, true);  // 4

            //건축가에게 알림 객체 생성하도록
            Notification notification=builder.build();

            //알림매니저에게 알림(Notify) 요청
            notificationManager.notify(1001, notification);

            thread.stopForever();

        }else if(AMSettings.workOutTimeChk.equals("on")){

            thread.stopForever();
        }
        //알림 요청시에 사용한 번호를 알림제거 할 수 있음.
        return "ss";

    }

    public class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
            Intent intent = new Intent( MyService.this, MainActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            PendingIntent pendingIntent = PendingIntent.getActivity( MyService.this, 0, intent, PendingIntent.FLAG_MUTABLE );
            Uri soundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant")
                NotificationChannel notificationChannel = new NotificationChannel( "my_notification", "n_channel", NotificationManager.IMPORTANCE_MAX );
                notificationChannel.setDescription( "description" );
                notificationChannel.setName( "Channel Name" );
                assert notificationManager != null;
                notificationManager.createNotificationChannel( notificationChannel );
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( MyService.this )
                    .setSmallIcon( R.drawable.common_google_signin_btn_icon_dark )
                    .setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.common_google_signin_btn_icon_dark ) )
                    .setContentTitle( "Title" )
                    .setContentText( "ContentText" )
                    .setAutoCancel( true )
                    .setSound( soundUri )
                    .setContentIntent( pendingIntent )
                    .setDefaults( Notification.DEFAULT_ALL )
                    .setOnlyAlertOnce( true )
                    .setChannelId( "my_notification" )
                    .setColor( Color.parseColor( "#ffffff" ) );
            //.setProgress(100,50,false);
            assert notificationManager != null;
            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            Calendar cal = Calendar.getInstance();
            int hour = cal.get( Calendar.HOUR_OF_DAY );
            if (hour == 18) {
                notificationManager.notify( m, notificationBuilder.build() );
                thread.stopForever();
            } else if (hour == 22) {
                notificationManager.notify( m, notificationBuilder.build() );
                thread.stopForever();
            }
        }
    }
}