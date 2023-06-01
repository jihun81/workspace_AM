package com.metanetglobal.knowledge.worker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.metanetglobal.knowledge.worker.InterceptorActivity;
import com.metanetglobal.knowledge.worker.R;
import com.metanetglobal.knowledge.worker.common.BusProvider;
import com.metanetglobal.knowledge.worker.intro.IntroActivity;
import com.metanetglobal.knowledge.worker.main.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendNotification(remoteMessage.getNotification().getBody());
    }

    @Override
    public void onNewToken(String token) {
        // 토큰이 업데이트될 때 호출되는 메소드
        // 새로운 토큰을 서버에 등록하거나 다른 작업을 수행할 수 있습니다.
    }

    private void sendNotification(String messageBody) {
        //알림(Notification)을 관리하는 관리자 객체를 운영체제(Context)로부터 소환하기
        NotificationManager notificationManager=(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
        NotificationCompat.Builder builder= null;
        Intent intent = new Intent(this, InterceptorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP );
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE); // FLAG_MUTABLE
        //PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        // 알림 액션 설정
        Intent actionIntent = new Intent(this, InterceptorActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, actionIntent,  PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_logo,
                "Action",
                actionPendingIntent)
                .build();

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelID="channel_01"; //알림채널 식별자
            String channelName="MyChannel01"; //알림채널의 이름(별명)
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
        builder.setSmallIcon(R.mipmap.ic_launcher);

        //상태바를 드래그하여 아래로 내리면 보이는
        //알림창(확장 상태바)의 설정
        builder.setContentTitle("퇴근확인");//알림창 제목
        builder.setContentText(messageBody);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);
      //  builder.addAction(action);

     //   builder.setFullScreenIntent(fullScreenPendingIntent, true);  // 4
        //건축가에게 알림 객체 생성하도록
        Notification notification=builder.build();

        //알림매니저에게 알림(Notify) 요청
        notificationManager.notify(1001, notification);

/*        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setFullScreenIntent(fullScreenPendingIntent, true);  // 4*/

/*

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/

        //사용자가 컴파일하는 SDK 버전을 정의하는 데 사용하는 compileSdkVersion보다 낮은 API 수준의 장치가 사용자에게있는 경우
        // 알림을 표시하기 위해 채널을 생성하는 코드를 실행하지 않도록 요청하는 코드.
        //오류가 발생하는데 해결방법을 찾지못해 주석처리함
//        if(Build.VERSION.SDK_INT  >= Build.VERSION_CODES.0) {
//
//        }

        //notificationManager.notify(0, notificationBuilder.build());

        notificationManager.notify(1001, notification);
    }
}
