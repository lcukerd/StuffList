package lcukerd.com.stufflist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Programmer on 10-04-2017.
 */

public class notifier extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(Context context,Intent intent)
    {
        Log.d("notification ", "called");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification_img)
                        .setContentTitle("Items Left for " + intent.getStringExtra("Event"));

        DBinteract interact = new DBinteract(context);
        Cursor cursor = interact.readinEvent(intent.getStringExtra("Event"),eventDBcontract.ListofItem.columndatetime+" ASC");
        String infoDB[] = new String[3];
        int ti = 0, ri = 0;
        Log.d("notification",String.valueOf(cursor.getCount()));
        while(cursor.moveToNext())
        {
            infoDB[0] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken));
            infoDB[1] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn));
            infoDB[2] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName));

            if (infoDB[2].length() >= 2)
                if ((infoDB[2].charAt(0) != '#') && (infoDB[2].charAt(1) != '%')) {
                    if (infoDB[0].equals("0"))
                        ti++;
                    if ((infoDB[0].equals("1")) && (infoDB[1].equals("0")))
                        ri++;
                }
        }
        if (intent.getStringExtra("partoftrip").equals("leave"))
            mBuilder.setContentText("Good Luck for " + intent.getStringExtra("Event") + " You forgot to take " + ti + " items.");
        else
            mBuilder.setContentText("Hope you enjoyed " + intent.getStringExtra("Event") + " You forgot to take " + ri + " items.");

        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        Intent resultIntent = new Intent(context, showList.class);
        resultIntent.putExtra("Event_Name",intent.getStringExtra("Event"));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(showList.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(5, mBuilder.build());
    }

}
