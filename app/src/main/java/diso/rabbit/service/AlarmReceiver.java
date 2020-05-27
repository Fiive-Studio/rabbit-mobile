package diso.rabbit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import diso.rabbit.R;
import diso.rabbit.gui.HomeGUI;
import diso.rabbit.helper.Enums;
import diso.rabbit.helper.Utils;

/**
 * Created by pabdiava on 1/04/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            AlarmUtil.SetFutureAlarms(context);
        }
        else {
            Intent service = new Intent(context, TaskNotificationService.class);
            service.putExtras(intent.getExtras());
            context.startService(service);
        }
    }
}
