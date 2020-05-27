package diso.rabbit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import diso.rabbit.R;
import diso.rabbit.data.Alarm;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.gui.HomeGUI;
import diso.rabbit.gui.TaskAddEditGUI;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;

/**
 * Created by pabdiava on 1/04/2016.
 */
public class TaskNotificationService extends Service {

    private NotificationManager mManager;
    AlarmDao alarmDao;
    Alarm alarm;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {
            long id = intent.getExtras().getLong(Enums.Fields.AlarmId.toString());

            Intent intent1 = new Intent(this.getApplicationContext(), TaskAddEditGUI.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(TaskAddEditGUI.class);
            stackBuilder.addNextIntent(intent1);

            Bundle bundle = new Bundle();
            bundle.putLong(Enums.Fields.AlarmId.toString(), id);
            intent1.putExtras(bundle);
            PendingIntent pIntent = stackBuilder.getPendingIntent((int) id, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmDao = Utils.GetDaoSession(this.getApplicationContext()).getAlarmDao();
            alarm = alarmDao.queryBuilder().where(AlarmDao.Properties.Id.eq(id)).unique();

            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit_notification2);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext());
            builder.setContentTitle(alarm.getTitle())
                    .setContentText(alarm.getDescription())
                    .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(alarm.getDescription())
                                .setBigContentTitle(alarm.getTitle())
                    )
                    .setTicker(String.format(getString(R.string.app_notification),alarm.getCourse().getName()))
                    .setSmallIcon(R.drawable.rabbit_notification)
                    .setLargeIcon(bm)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setGroup(getString(R.string.app_group_key));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                builder = builder.setColor(getResources().getColor(R.color.colorPrimary));
            }

            NotificationManager notificationManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.notify((int)id, builder.build());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

