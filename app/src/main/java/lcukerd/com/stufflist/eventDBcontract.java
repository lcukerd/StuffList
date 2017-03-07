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
                    ListofItem.columnID+ " INTEGER PRIMARY KEY," +
                    ListofItem.columntaken + " INTEGER," +
                    ListofItem.columnreturn + " INTEGER, " +
                    ListofItem.columnName + " TEXT );";

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
                columntaken = "Item_taken",
                columnreturn = "Item_brought_back",
                columnName = "Name_of_Item";
    }

/*    void saveImage(Context context)
    {
        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;
        byte b;

        String ret = "";

        try {
            outputStream = openFileOutput("try.txt", Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream = context.openFileInput("check");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
                Log.d("message is",ret);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }
*/
}