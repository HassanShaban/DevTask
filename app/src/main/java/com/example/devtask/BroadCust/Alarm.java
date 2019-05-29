package com.example.devtask.BroadCust;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.devtask.DataBase.TaskDB;
import com.example.devtask.MainActivity;
import com.example.devtask.R;
import com.example.devtask.interfaces.PassDataInterface;
import com.example.devtask.models.TaskModel;
import com.example.devtask.repository.CallingAPI;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.BooleanSupplier;

public class Alarm extends BroadcastReceiver implements PassDataInterface {

    PassDataInterface passDataInterface;
    CallingAPI callingAPI;
    TaskDB taskDB;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {

        passDataInterface = Alarm.this;
        callingAPI = new CallingAPI(passDataInterface);
        taskDB = TaskDB.getInstance(context);
        callingAPI.getData("e37f6859ec422d0609ba42de7820eb4ec94af9f7" , 0 ,10);
        this.context = context;
    }


    public static void setAlarm(Context context){

        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setInexactRepeating(AlarmManager.RTC,
                SystemClock.elapsedRealtime(),
                60*60*1000,
                pi);

    }

    @Override
    public void passData(ArrayList<TaskModel> data, String state) {


        taskDB.deleteData();
        taskDB.insertData(data);

        if(data.size() == 8){

             showNotification("لا يوجد بيانات جديدة");
        }
        else if(data.size() > 8){

            showNotification("يوجد بيانات جديدة");

        }

    }

    private void showNotification(String body) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        Intent notificationIntent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        String CHANNEL_ID = "my_channel_01";
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "dexef", importance);

            nm.createNotificationChannel(mChannel);
        }


        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Task nitification")
                .setChannelId(CHANNEL_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(body).build();

        Notification n = builder.getNotification();
        n.defaults |= Notification.DEFAULT_SOUND;
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        nm.notify(m, n);
        wl.release();


    }


}
