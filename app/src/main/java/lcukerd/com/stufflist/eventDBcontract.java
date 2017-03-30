package lcukerd.com.stufflist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Programmer on 03-03-2017.
 */

public class eventDBcontract extends SQLiteOpenHelper{

     private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ListofItem.tableName + " (" +
                    ListofItem.columnID + " INTEGER PRIMARY KEY," +
                    ListofItem.columnEvent + " TEXT, " +
                    ListofItem.columnName + " TEXT, " +
                    ListofItem.columntaken + " INTEGER," +
                    ListofItem.columnreturn + " INTEGER, " +
                    ListofItem.columnFileloc + " TEXT, " +
                    ListofItem.columndatetime + " INTEGER );";
    private ContentValues values;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Stuff.db";

    public eventDBcontract(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
     public void onCreate(SQLiteDatabase db)
     {
         db.execSQL(SQL_CREATE_ENTRIES);
         Log.d("Database","created");
         values = new ContentValues();
         values.put(eventDBcontract.ListofItem.columnEvent,"Titorizl");
         db.insert(eventDBcontract.ListofItem.tableName,null,values);
         for (int i=0;i<12;i++) {
             values = new ContentValues();
             adddummyitem("Item " + String.valueOf(i), i % 2, i % 3, R.drawable.d1 + i);
             db.insert(eventDBcontract.ListofItem.tableName,null,values);
         }

     }
     void adddummyitem(String name,int t,int r,int id)
     {
         values.put(eventDBcontract.ListofItem.columnEvent,"Sample");
         values.put(eventDBcontract.ListofItem.columnName,name);
         values.put(eventDBcontract.ListofItem.columndatetime,getmillis());
         values.put(eventDBcontract.ListofItem.columntaken,String.valueOf(t));
         values.put(eventDBcontract.ListofItem.columnreturn,String.valueOf(r));
         Uri path = Uri.parse("android.resource://lcukerd.com.stufflist/" + id);
         values.put(eventDBcontract.ListofItem.columnFileloc, path.toString());
     }
     private long getmillis()
     {
         Calendar c = Calendar.getInstance();
         return c.getTimeInMillis();
     }

     public void onUpgrade(SQLiteDatabase db,int oldVersion , int newVersion)
     {
         Log.d("Database","upgraded");
     }

    public static class ListofItem
    {
        public static final String tableName = "List_of_Item",
                columnID="ID",
                columnEvent = "Name_of_event",
                columntaken = "Item_taken",
                columnreturn = "Item_brought_back",
                columnName = "Name_of_Item",
                columnFileloc = "File_Location",
                columndatetime = "Creation_millisec";
    }
}
