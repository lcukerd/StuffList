package lcukerd.com.stufflist;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Scroller;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.IOException;

public class showList extends AppCompatActivity {

    private GridLayout gridLayout;
    private LinearLayout l1 , l2 , l3;
    private View v;
    private CardView cardView;
    private Cursor cursor;
    private String data;
    private DBinteract interact = new DBinteract(this);
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private EditText notes ;
    private String specialid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        data = intent.getStringExtra("Event_Name");
    }
    protected void onResume()
    {
        super.onResume();

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String order = preferences.getString("pref_item_order",getString(R.string.defaultvai));
        order = updateorder(order);

        setContentView(R.layout.activity_show_list);
        getSupportActionBar().setTitle(data);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33ff4444")));
        gridLayout = (GridLayout) findViewById(R.id.grid);

        int i=1;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int columns,w,h;

        if (metrics.heightPixels>=metrics.widthPixels)
        {
            columns=2;
            w= metrics.widthPixels/columns;
            h= metrics.heightPixels/columns;
            gridLayout.setColumnCount(columns);
            l1 = new LinearLayout(this);
            l3 = new LinearLayout(this);
            l1.setOrientation(LinearLayout.VERTICAL);
            l3.setOrientation(LinearLayout.VERTICAL);
            gridLayout.addView(l1);
            gridLayout.addView(l3);
        }
        else
        {
            columns=3;
            h= metrics.widthPixels/columns;
            w= metrics.widthPixels/columns;
            gridLayout.setColumnCount(columns);
            l1 = new LinearLayout(this);
            l2 = new LinearLayout(this);
            l3 = new LinearLayout(this);
            l1.setOrientation(LinearLayout.VERTICAL);
            l2.setOrientation(LinearLayout.VERTICAL);
            l3.setOrientation(LinearLayout.VERTICAL);
            gridLayout.addView(l1);
            gridLayout.addView(l2);
            gridLayout.addView(l3);
        }

        v =  View.inflate(this,R.layout.customnote,null);
        FrameLayout frameLayout = (FrameLayout) v.findViewById(R.id.frame);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(w,h/3);
        frameLayout.setLayoutParams(param);
        notes = (EditText) v.findViewById(R.id.addnote);
        /*notes.setScroller(new Scroller(this));
        notes.setMaxLines(4);
        notes.setVerticalScrollBarEnabled(true);
        notes.setMovementMethod(new ScrollingMovementMethod());*/
        notes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() == R.id.addnote) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        l3.addView(frameLayout);

        int th,tw,c1=0,c2=0,c3=h/3;
        cursor = interact.readinEvent(data,order);

        while(cursor.moveToNext())                                                                  // To display list
        {
            tw = w;
            th = h;
            v =  View.inflate(this,R.layout.trying,null);
            frameLayout = (FrameLayout) v.findViewById(R.id.frame);

            cardView = (CardView) v.findViewById(R.id.cardSample);
            final String ct = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken));
            final String rt = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn));
            final String photoURI = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc));
            final String name = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName));
            final String id = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID));

            if (name.length()>=2)
                if ((name.charAt(0)=='#')&&(name.charAt(1)=='%'))
                  {
                      specialid =id;
                      notes.setText(name.substring(2));
                      Log.d("found special id",String.valueOf(id));
                      continue;
                  }
            Log.d("id",String.valueOf(id));
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
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        Log.d("Long Click","successful");
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup,(ViewGroup)findViewById(R.id.pop));
                        PopupWindow pw = new PopupWindow(layout, 400, 200, true);
                        int coord[]= new int[2];
                        v.getLocationOnScreen(coord);
                        pw.showAtLocation(v, Gravity.NO_GRAVITY, coord[0] + 50 ,coord[1]+100);
                        Button del = (Button) layout.findViewById(R.id.del);
                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteDatabase db = dBcontract.getWritableDatabase();
                                Log.d("delete operaiton",String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,eventDBcontract.ListofItem.columnID+" = "+id,null)));
                                recreate();                                                         //add option to delete files as well
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            final TextView Ename = (TextView) v.findViewById(R.id.textView);
            final CheckBox taken  = (CheckBox) v.findViewById(R.id.taken),returned = (CheckBox) v.findViewById(R.id.returned);
            final ImageView Eimage = (ImageView) v.findViewById(R.id.imageView);

            taken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ct.equals(String.valueOf(taken.isChecked()))==false)
                    {
                        if (photoURI!=null)
                            interact.save(data,Ename.getText().toString(),taken,returned,Uri.parse(photoURI),"update",id);
                        else
                            interact.save(data,Ename.getText().toString(),taken,returned,null,"update",id);
                    }
                }
            });
            returned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rt.equals(String.valueOf(returned.isChecked()))==false)
                    {
                        if (photoURI!=null)
                            interact.save(data,Ename.getText().toString(),taken,returned,Uri.parse(photoURI),"update",id);
                        else
                            interact.save(data,Ename.getText().toString(),taken,returned,null,"update",id);
                    }
                }
            });

            if(photoURI!=null) {
                try {

                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoURI));
                    Log.d("Size of image", "width:"+photo.getWidth()+" height:"+photo.getHeight());
                    if ((metrics.heightPixels>metrics.widthPixels)&&(photo.getHeight()<photo.getWidth()))
                    {
                        th =(int) ( tw* ( ((float)photo.getHeight()) / ((float)photo.getWidth()) ));
                        Log.d("Metrics for landscape",String.valueOf(tw)+" "+String.valueOf(th));
                    }
                    BitmapDrawable ob = new BitmapDrawable(getResources(), photo);
                    Eimage.setBackground(ob);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Couldn't Load",name+" "+photoURI);
                }
            }
            else
            {

            }

            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(tw,th));

            if (name.equals("")==false)
                Ename.setText(name);                                                                    //adds data in card

            if (ct.equals(String.valueOf(1)))
                taken.setChecked(true);
            else
                taken.setChecked(false);
            if (rt.equals(String.valueOf(1)))
                returned.setChecked(true);
            else
                returned.setChecked(false);

            if (columns==3) {
                if ((c1 < c2) && (c1 < c3))
                    i = 1;
                else if ((c2 < c1) && (c2 < c3))
                    i = 2;
                else if ((c3<c1)&&(c3<c2))
                    i = 3;
                else if (c2 == c3)
                    i = 2;
                else
                    i = 1;
            }
            else if (columns==2)
            {
                if (c1<=c3)
                    i = 1;
                else
                    i = 2;
            }

            if (i==columns)                                                                         // adding rows
            {
                c3+=th;
                l3.addView(v);
                i=1;
            }
            else if (i==1)
            {
                c1+=th;
                l1.addView(v);
                i++;
            }
            else if (i<columns)
            {
                c2+=th;
                l2.addView(v);
                i++;
            }
        }
        if (specialid==null)
        {
            interact.save(data,"#%just created",new CheckBox(this),new CheckBox(this),null,"main","0");
        }
    }

    protected  void onStop()
    {
        super.onStop();
        interact.saveEvent(data,notes.getText().toString(),0,0,specialid);
    }
    public String updateorder(String order)
    {
        if (order.equals("date_asc_item"))
            order = eventDBcontract.ListofItem.columndatetime+" ASC";
        else if (order.equals("date_desc_item"))
            order = eventDBcontract.ListofItem.columndatetime+" DESC";
        else if (order.equals("name_asc_item"))
            order = eventDBcontract.ListofItem.columnName+" ASC";
        else if (order.equals("name_desc_item"))
            order = eventDBcontract.ListofItem.columnName+" DESC";
        return order;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent addItem = new Intent(getApplicationContext(),addItem.class);
            addItem.putExtra("eventName",data);
            addItem.putExtra("calledby","main");
            startActivity(addItem);
            return true;
        }
        else if (id == R.id.action_settings)
        {
            startActivity(new Intent(this,orderitem.class));
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }

}