package lcukerd.com.stufflist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
                        .setSmallIcon(R.drawable.ic_card_travel_black_24dp)
                        .setContentTitle("Items Left for " + intent.getStringExtra("Event"))
                        .setContentText("Good Luck for " + intent.getStringExtra("Event") + " You forgot to take " + intent.getStringExtra("Items_left") + " items.");
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
