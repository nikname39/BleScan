package com.minewbeacon.blescan.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.minewbeacon.blescan.demo.activity.attendance;
import com.yuliwuli.blescan.demo.R;

public class MyService extends Service {

    BackgroundTask task;

    int value = 0;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//반복작업
        //task = new BackgroundTask();
        //task.execute();


        initializeNotification(); //포그라운드 생성



        return START_REDELIVER_INTENT;
    }

    /**
     * 포그라운드 서비스
     */
    public void initializeNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "3");
        builder.setSmallIcon(R.drawable.bgs);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("메인화면을 보려면 누르세요.");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);

        // 버튼 등록
        builder.addAction(makeButtonInNotification("Start"));
        builder.addAction(makeButtonInNotification("Pause"));
        builder.addAction(makeButtonInNotification("Stop"));

        Intent notificationIntent = new Intent(this, attendance.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("3", "포그라운드 서비스", NotificationManager.IMPORTANCE_NONE));

        }
        Notification notification = builder.build();
        startForeground(3, notification);
    }

    class BackgroundTask extends AsyncTask<Integer, String, Integer> {

        String result = "";

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... values) {

            while(isCancelled() == false){

                try{
                    println(value + "번째 실해중");
                    Thread.sleep(1000);
                    value++;
                }catch (InterruptedException ex){}
            }
            return value;
        }

        //상태확인
        @Override
        protected void onProgressUpdate(String... String) {
            println("onProgressUpdate()업데이트");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            println("onPostExecute()");
            value = 0;
        }

        @Override
        protected void onCancelled() {
            value = 0; //정지로 초기화
        }
    }

    /**
     * 서비스 종료
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestory");

        task.cancel(true);
    }


    public void println(String message){
        Log.d("MyService", message);
    }

    private NotificationCompat.Action makeButtonInNotification(String action) {
        // PendingIntent로 등록될 Intent 생성
        Intent intent = new Intent(getBaseContext(), MyService.class);
        // Intent로 전달될 액션 설정
        intent.setAction(action);

        // PendingIntent 생성
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 1, intent, 0);

        // 임의의 버튼 아이콘 등록
        int iconId = android.R.drawable.ic_media_pause;

        // 버튼 타이틀 등록
        String btnTitle = action;

        // 해당 버튼 액션 설정
        NotificationCompat.Action notifAction
                = new NotificationCompat.Action.Builder(iconId, btnTitle, pendingIntent).build();

        return notifAction;
    }
}












