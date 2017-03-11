package lcukerd.com.stufflist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.drawable.ic_input_add;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.rgb;

public class showList extends AppCompatActivity {

    private LinearLayout linearLayout;
    private View v;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);

        linearLayout = (LinearLayout) findViewById(R.id.linear);

        Intent intent = getIntent();
        String data = intent.getStringExtra("Event_Name");
        Log.d("Event Name",data);
        readDB(data);
    }
    protected void onStart()
    {
        super.onStart();
        while(cursor.moveToNext())
        {
            v =  View.inflate(this,R.layout.temp,null);
            TextView Ename = (TextView) v.findViewById(R.id.textView);
            CheckBox taken  = (CheckBox) v.findViewById(R.id.taken),returned = (CheckBox) v.findViewById(R.id.returned);
            ImageView Eimage = (ImageView) v.findViewById(R.id.imageView);
            String ct = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken));
            String rt = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn));
            String photoURI = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc));

            Ename.setText(cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName)));

            if (ct.equals(String.valueOf(1)))
                taken.setChecked(true);
            else
                taken.setChecked(false);
            if (rt.equals(String.valueOf(1)))
                returned.setChecked(true);
            else
                returned.setChecked(false);
            if(photoURI!=null) {
                try {

                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoURI));
                    Log.d("Size of image", "width:"+photo.getWidth()+" height:"+photo.getHeight());
                    Eimage.setImageBitmap(Bitmap.createScaledBitmap(photo,720,1280,false));      // Image gets cropped look into it
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            linearLayout.addView(v);
        }
    }

    private void readDB(String event)
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        String q = "Select * From "+eventDBcontract.ListofItem.tableName+" where "+eventDBcontract.ListofItem.columnEvent+" = '"+event+"'";
        Log.d("Query ", q);

        cursor = db.rawQuery(q,null);


    }


}