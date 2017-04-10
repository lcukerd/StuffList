package lcukerd.com.stufflist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;
import android.widget.CheckBox;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Programmer on 13-03-2017.
 */

public class DBinteract {

    private eventDBcontract dBcontract ;
    private         String[] projection = {
            eventDBcontract.ListofItem.columnID,
            eventDBcontract.ListofItem.columnEvent,
            eventDBcontract.ListofItem.columnName,
            eventDBcontract.ListofItem.columntaken,
            eventDBcontract.ListofItem.columnreturn,
            eventDBcontract.ListofItem.columnFileloc,
            eventDBcontract.ListofItem.columndatetime,
            eventDBcontract.ListofItem.columnnotes
    };

    DBinteract(Context context)
    {
        dBcontract = new eventDBcontract(context);
    }

    public String[] readfromDB(String order)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();

        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,null,null,null,null,order);

        while(cursor.moveToNext())
        {
            Log.d("column return",cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columndatetime))+" "+
                    cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnnotes)));
        }

        cursor = db.query(eventDBcontract.ListofItem.tableName,projection,null,null,eventDBcontract.ListofItem.columnEvent,null,order);

        int i=0;
        String NameofEvents[] = new String[cursor.getCount()];
        while(cursor.moveToNext())
        {
            NameofEvents[i] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent));
            i++;
        }
        return (NameofEvents);
    }


    public Cursor readinEvent(String event,String order)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,eventDBcontract.ListofItem.columnEvent+" = '"+event+"'",null,null,null,order);
        return cursor;
    }


    public void save(String eventName, String itemName, CheckBox taken, CheckBox returned, Uri photoURI , String caller , String id)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnEvent,eventName);
        values.put(eventDBcontract.ListofItem.columnName,itemName);
        values.put(eventDBcontract.ListofItem.columndatetime,getmillis());
        if (taken.isChecked())
            values.put(eventDBcontract.ListofItem.columntaken,"1");
        else
            values.put(eventDBcontract.ListofItem.columntaken,"0");
        if (returned.isChecked())
            values.put(eventDBcontract.ListofItem.columnreturn,"1");
        else
            values.put(eventDBcontract.ListofItem.columnreturn,"0");
        if (photoURI!=null) {
            values.put(eventDBcontract.ListofItem.columnFileloc, photoURI.toString());
            Log.d("File address write", photoURI.toString());
        }
        else
            values.putNull(eventDBcontract.ListofItem.columnFileloc);
        if (caller.equals("main"))
        {
            db.insert(eventDBcontract.ListofItem.tableName,null,values);
            Log.d("save operation","complete");
        }
        else
        {
            db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id});
            Log.d("update operation","complete");
        }
    }

    public void saveEvent(String eventName , String itemName , long start , long end , String id)
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnEvent,eventName);
        values.put(eventDBcontract.ListofItem.columnName,"#%"+itemName);
        if (start!=-1)
            values.put(eventDBcontract.ListofItem.columntaken,start);
        if (end!=-1)
            values.put(eventDBcontract.ListofItem.columnreturn,end);

        db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id});

        Log.d("save operation","Event detail complete");
    }
    public void savenote(String id , String note)
    {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = dBcontract.getWritableDatabase();

        values.put(eventDBcontract.ListofItem.columnnotes,note);
        db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id});
        Log.d("Interact","add note complete");

    }
    private long getmillis()
    {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

}
