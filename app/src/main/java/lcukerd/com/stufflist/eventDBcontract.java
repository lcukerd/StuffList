package lcukerd.com.stufflist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                    ListofItem.columnFileloc + " TEXT );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ListofItem.tableName;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Stuff.db";

    public eventDBcontract(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
     public void onCreate(SQLiteDatabase db)
     {
         db.execSQL(SQL_CREATE_ENTRIES);
     }
     public void onUpgrade(SQLiteDatabase db,int oldVersion , int newVersion)
     {

     }

    public static class ListofItem
    {
        public static final String tableName = "List_of_Item",
                columnID="ID",
                columnEvent = "Name_of_event",
                columntaken = "Item_taken",
                columnreturn = "Item_brought_back",
                columnName = "Name_of_Item",
                columnFileloc = "File_Location";
    }
}
