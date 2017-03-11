package lcukerd.com.stufflist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;

public class showList extends AppCompatActivity {

    private LinearLayout linearLayout;
    private View v;
    private CardView cardView;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private Cursor cursor;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        data = intent.getStringExtra("Event_Name");
    }
    protected void onResume()
    {
        super.onResume();
        setContentView(R.layout.activity_show_list);
        linearLayout = (LinearLayout) findViewById(R.id.linear);

        readDB(data);
        while(cursor.moveToNext())
        {
            v =  View.inflate(this,R.layout.temp,null);
            cardView = (CardView) v.findViewById(R.id.cardSample);
            final String ct = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken));
            final String rt = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn));
            final String photoURI = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc));
            final String name = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName));
            final String id = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID));

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),addItem.class);
                    intent.putExtra("eventName",data);
                    intent.putExtra("calledby","list");
                    intent.putExtra("available data",new String[]{ name , ct , rt , photoURI , id });
                    startActivity(intent);
                }
            });

            TextView Ename = (TextView) v.findViewById(R.id.textView);
            CheckBox taken  = (CheckBox) v.findViewById(R.id.taken),returned = (CheckBox) v.findViewById(R.id.returned);
            ImageView Eimage = (ImageView) v.findViewById(R.id.imageView);

            Ename.setText(name);

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
                    Eimage.setImageBitmap(photo);      // Image gets cropped look into it
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
        cursor = db.rawQuery(q,null);
    }
}