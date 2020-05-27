package diso.rabbit.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;
import java.util.List;

import diso.rabbit.data.Alarm;
import diso.rabbit.data.AlarmDao;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;

/**
 * Created by pabdiava on 17/04/2016.
 */
public class AlarmUtil {

    public static void setAlarm(Context packageContext, Object alarmService, long time, long idAlarm){
        Intent intent = getIntent(packageContext, idAlarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(packageContext, (int)idAlarm, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)alarmService;
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    public static void cancelAlarm(Context packageContext, Object alarmService, long idAlarm){
        Intent intent = getIntent(packageContext, idAlarm);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(packageContext, (int)idAlarm, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)alarmService;
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void cancelNotification(Context ctx, long idAlarm) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel((int)idAlarm);
    }

    public static void cancelAllNotifications(Context ctx) {
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    public static Intent getIntent(Context packageContext, long idAlarm){
        Intent intent = new Intent(packageContext, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putLong(Enums.Fields.AlarmId.toString(), idAlarm);
        intent.putExtras(bundle);

        return intent;
    }

    public static void SetFutureAlarms(Context ctx){
        AlarmDao alarmDao = Utils.GetDaoSession(ctx).getAlarmDao();
        List<Alarm> data = alarmDao.queryBuilder()
                .where(AlarmDao.Properties.AlarmDate.ge(Calendar.getInstance().getTime()))
                .list();

        if(data != null) {
            for (Alarm alarm : data) {
                AlarmUtil.setAlarm(ctx, ctx.getSystemService(ctx.ALARM_SERVICE), alarm.getAlarmDate().getTime(), alarm.getId());
            }
        }
    }
}
