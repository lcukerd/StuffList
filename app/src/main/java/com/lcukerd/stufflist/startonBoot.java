package com.lcukerd.stufflist;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lcukerd.stufflist.database.eventDBcontract;

/**
 * Created by Programmer on 11-04-2017.
 */

public class startonBoot extends BroadcastReceiver {
    eventDBcontract dBcontract;
    private AlarmManager notifalm;
    private String[] projection = {
            eventDBcontract.ListofItem.columnEvent,
            eventDBcontract.ListofItem.columntaken,
            eventDBcontract.ListofItem.columnreturn,
    };
    private String event = ".............";
    int ti=0;
    int ri=0;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            dBcontract = new eventDBcontract(context);
            SQLiteDatabase db = dBcontract.getReadableDatabase();
            Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,null,null,null,null,eventDBcontract.ListofItem.columnEvent+" ASC");
            while (cursor.moveToNext())
            {
                long t = Long.parseLong(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken))),
                     r = Long.parseLong(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn)));

                if (t>System.currentTimeMillis()) {
                    PendingIntent al = createintent(context,cursor,"leave");
                    Log.d("startonboot:","leave " + String.valueOf(t) );
                    notifalm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    notifalm.setExact(AlarmManager.RTC_WAKEUP, ((t - System.currentTimeMillis()) > 3600000 ? t - 3600000 : (System.currentTimeMillis() + 36000)), al);
                }
                if (r>System.currentTimeMillis()) {
                    PendingIntent al = createintent(context,cursor,"return");
                    Log.d("startonboot:","return " + String.valueOf(r) );
                    notifalm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    notifalm.setExact(AlarmManager.RTC_WAKEUP, ((r - System.currentTimeMillis()) > 3600000 ? r - 3600000 : (System.currentTimeMillis() + 36000)), al);
                }

            }

        }
    }
    PendingIntent createintent(Context context,Cursor cursor,String n)
    {
        Intent alarmclass = new Intent(context, notifier.class);
        alarmclass.putExtra("Event", cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent)));
        alarmclass.putExtra("partoftrip", n );
        alarmclass.setAction(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent)));                                             //To distinguish between alarms for diff event
        PendingIntent al = PendingIntent.getBroadcast(context, 0, alarmclass, 0);
        return al;
    }
}
