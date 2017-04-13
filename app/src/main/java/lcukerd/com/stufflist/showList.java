package lcukerd.com.stufflist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class showList extends AppCompatActivity {

    private GridLayout gridLayout;
    private LinearLayout l1 , l2 , l3;
    private int j=0;
    private int columns,w,h,c1=0,c2=0,c3=0,i=0;
    private View v;
    private CardView cardView;
    private Cursor cursor;
    private String data;
    private DBinteract interact = new DBinteract(this);
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private EditText notes ;
    private String specialid = null;
    private FrameLayout frameLayout;
    private DisplayMetrics metrics;
    private Context context = this;
    private Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat sdf;
    private String hometime=null,hoteltime=null;
    private AlarmManager notifalm;
    int ti=0;
    int ri=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        data = intent.getStringExtra("Event_Name");
    }
    protected void onResume()
    {
        super.onResume();

        setContentView(R.layout.activity_show_list);                                                    //change to activity_show_list and remove fabs
                                                                                                    //part of code to fix small icon problem

        j=0;
        Log.d("List","started");
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String order = preferences.getString("pref_item_order",getString(R.string.defaultvai));
        order = updateorder(order);

        getSupportActionBar().setTitle(data);
        gridLayout = (GridLayout) findViewById(R.id.grid);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabl);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addItem = new Intent(getApplicationContext(),addItem.class);
                addItem.putExtra("eventName",data);
                addItem.putExtra("calledby","main");
                startActivity(addItem);
            }
        });*/

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        columns=0;
        w=h=c1=c2=c3=i=0;

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
            h= (2*metrics.widthPixels)/columns;
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
        cursor = interact.readinEvent(data,order);

        v =  View.inflate(this,R.layout.customnote,null);
        frameLayout = (FrameLayout) v.findViewById(R.id.frame);
        FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(w,h/3);
        frameLayout.setLayoutParams(param);
        notes = (EditText) v.findViewById(R.id.addnote);
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


        c3=h/3;
        int tw=0,th=0;

        Log.d("no. of rows",String.valueOf(cursor.getCount()-1));
        loadList bklistgene;

        ti=0;
        ri=0;
        while(cursor.moveToNext())                                                                  // To display list
        {
            bklistgene =  new loadList();
            final String infoDB[]= new String[7];
            infoDB[0] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken));
            infoDB[1] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn));
            infoDB[2] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc));
            infoDB[3] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName));
            infoDB[4] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID));
            infoDB[5] = data;
            infoDB[6] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnnotes));
            if (infoDB[0].equals("0"))
                ti++;
            if (infoDB[1].equals("0"))
                ri++;
            if(cursor.isLast())
                bklistgene.execute(infoDB[0],infoDB[1],infoDB[2],infoDB[3],infoDB[4],infoDB[5],infoDB[6],"last");
            else
                bklistgene.execute(infoDB[0],infoDB[1],infoDB[2],infoDB[3],infoDB[4],infoDB[5],infoDB[6],"not last");
        }

    }

    private void scheduler()
    {
        final AlertDialog.Builder eventName = new AlertDialog.Builder(context,R.style.dialogStyle);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View dialogb = inflater.inflate(R.layout.dialog_schedule, null);
        eventName.setView(dialogb);
        final AlertDialog dialog = eventName.create();
        dialog.setTitle("When will you leave");
        dialog.show();
        if ((hometime == null)&&(hoteltime == null)) {
            final AlertDialog.Builder instruct = new AlertDialog.Builder(this);
            instruct.setMessage(R.string.notification_instruct)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog instructd = instruct.create();
            instructd.show();
        }
        Button home = (Button) dialogb.findViewById(R.id.home);
        Button hotel = (Button) dialogb.findViewById(R.id.hotel);

        if (hometime!=null)
            home.setText(hometime);
        if (hoteltime!=null)
            hotel.setText(hoteltime);
        showdialog(home);
        showdialog(hotel);
    }

    private void showdialog(final Button btn)
    {
        final DatePickerDialog.OnDateSetListener datehome = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                final String myFormat = "h:mm a, EEE, d MMM yyyy";
                sdf = new SimpleDateFormat(myFormat);
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog timepicker =new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY,selectedHour);
                        myCalendar.set(Calendar.MINUTE,selectedMinute);
                        btn.setText(sdf.format(myCalendar.getTime()));
                        int itemLeft;
                        if (btn.getId()==R.id.home)
                        {
                            hometime = sdf.format(myCalendar.getTime());
                            itemLeft = ti;
                            interact.saveEvent(data,notes.getText().toString(),myCalendar.getTimeInMillis(),-1,specialid);
                        }
                        else {
                            itemLeft = ri;
                            hoteltime = sdf.format(myCalendar.getTime());
                            interact.saveEvent(data, notes.getText().toString(), -1, myCalendar.getTimeInMillis(), specialid);
                        }
                        if (myCalendar.getTimeInMillis()> System.currentTimeMillis()) {
                            Intent alarmclass = new Intent(context, notifier.class);
                            alarmclass.putExtra("Event", data);
                            Log.d("List: ","itemleft " + String.valueOf(itemLeft));
                            alarmclass.putExtra("Items_left", String.valueOf(itemLeft));
                            alarmclass.setAction(data);                                             //To distinguish between alarms for diff event
                            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, alarmclass, 0);
                            setalarm(alarmIntent);
                        }
                    }

                }, hour, minute, true);
                timepicker.show();
            }

        };
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, datehome, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    void setalarm(PendingIntent al)
    {
        Log.d("time set",String.valueOf((myCalendar.getTimeInMillis()- System.currentTimeMillis())>3600000?myCalendar.getTimeInMillis()-3600000:(System.currentTimeMillis()+36000)));
        notifalm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        notifalm.setExact(AlarmManager.RTC_WAKEUP,((myCalendar.getTimeInMillis()- System.currentTimeMillis())>3600000?myCalendar.getTimeInMillis()-3600000:(System.currentTimeMillis()+36000)),al);
    }
    protected  void onStop()
    {
        super.onStop();
        interact.saveEvent(data,notes.getText().toString(),-1,-1,specialid);
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
        else if (order.equals("forgot"))
            order = eventDBcontract.ListofItem.columntaken+" ASC";                                  //add condition to order by taken or return
        return order;
    }
    private void deleteimage(String imageloc)
    {
        ContentResolver imagefile = getContentResolver();
        Log.d("file deletion",String.valueOf(imagefile.delete(Uri.parse(imageloc),null,null))+" "+imageloc);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
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
        else if (id == R.id.about)
            startActivity(new Intent(this,about.class));
        else if (id == R.id.review) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
            }
        }
        else if (id == R.id.log)
            startActivity(new Intent(this,updateLog.class));
        else if (id == R.id.action_schedule)
            scheduler();
        return super.onOptionsItemSelected(item);
    }

    class loadList extends AsyncTask<String,Void,String[]>
    {
        private BitmapDrawable ob;
        private int tw,th;
        protected String[] doInBackground(String data[])
        {
            tw = w;
            th = h;
            if (data[3].length()>=2)
                if ((data[3].charAt(0)=='#')&&(data[3].charAt(1)=='%'))
                {
                    specialid = data[4];
                    Log.d("found special id",String.valueOf(data[4]));
                    this.cancel(true);
                    return data;                                                                    //return statement
                }

            if(data[2]!=null) {
                try {

                    Bitmap photo = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(data[2]));
                    if (photo.getHeight()>photo.getWidth())
                        photo = Bitmap.createScaledBitmap(photo,metrics.widthPixels/2,metrics.heightPixels/2,false);
                    else
                        photo = Bitmap.createScaledBitmap(photo,metrics.heightPixels/2,metrics.widthPixels/2,false);
                    Log.d("Size of image", "width:"+photo.getWidth()+" height:"+photo.getHeight());
                    if (metrics.heightPixels>metrics.widthPixels)
                    {
                        th =(int) ( tw* ( ((float)photo.getHeight()) / ((float)photo.getWidth()) ));
                        Log.d("Metrics for landscape",String.valueOf(tw)+" "+String.valueOf(th));
                    }
                    else
                    {
                        th =(int) ( tw* ( ((float)photo.getWidth()) / ((float)photo.getHeight())));
                        Log.d("Metrics for landscape",String.valueOf(tw)+" "+String.valueOf(th));

                    }
                    ob = new BitmapDrawable(getResources(), photo);                  //else pic was displayed over text

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Couldn't Load",data[3]+" "+data[2]);
                    th = th/3;
                }
            }
            else
            {
                th = th/2;
            }
            return data;

        }
        protected void onPostExecute(final String data[])
        {
            v =  View.inflate(context,R.layout.trying,null);
            frameLayout = (FrameLayout) v.findViewById(R.id.frame);

            cardView = (CardView) v.findViewById(R.id.cardSample);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),addItem.class);
                    intent.putExtra("eventName",data[5]);
                    intent.putExtra("calledby","list");
                    intent.putExtra("available data",new String[]{ data[3] , data[0] , data[1] , data[2] , data[4] });
                    startActivity(intent);
                }
            });
            final String thisimageuri = data[2];
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        Log.d("Long Click","successful");
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popl,(ViewGroup)findViewById(R.id.pop));
                        final PopupWindow pw = new PopupWindow(layout, 700 * (metrics.widthPixels/1080) , 300 * (metrics.heightPixels/1080) , true);
                        int coord[]= new int[2];
                        v.getLocationOnScreen(coord);
                        pw.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent)));
                        pw.setOutsideTouchable(true);
                        pw.showAtLocation(v, Gravity.NO_GRAVITY, coord[0] + 50 ,coord[1]+100);

                        Button del = (Button) layout.findViewById(R.id.del);
                        Button viewImage = (Button) layout.findViewById(R.id.view);
                        final EditText notes = (EditText) layout.findViewById(R.id.note);
                        notes.setText(data[6]);

                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteDatabase db = dBcontract.getWritableDatabase();
                                if ((thisimageuri!=null)&&(thisimageuri.toString().charAt(0)!='a'))
                                    deleteimage(thisimageuri);
                                Log.d("delete operaiton",String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,eventDBcontract.ListofItem.columnID+" = "+data[4],null)));
                                recreate();                                                         //add option to delete files as well
                            }
                        });

                        viewImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (thisimageuri!=null)
                                {
                                    Intent intent = new Intent(getApplicationContext(),showPic.class);
                                    intent.putExtra("photo uri",thisimageuri);
                                    pw.dismiss();
                                    startActivity(intent);
                                }
                            }
                        });

                        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                Log.d("popop window", "onDismiss: " + notes.getText().toString());
                                interact.savenote(data[4],notes.getText().toString());
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
                    if (data[0].equals(String.valueOf(taken.isChecked()))==false)
                    {
                        if (data[2]!=null)
                            interact.save(data[5],Ename.getText().toString(),taken,returned,Uri.parse(data[2]),"update",data[4]);
                        else
                            interact.save(data[5],Ename.getText().toString(),taken,returned,null,"update",data[4]);
                    }
                }
            });
            returned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data[1].equals(String.valueOf(returned.isChecked()))==false)
                    {
                        if (data[2]!=null)
                            interact.save(data[5],Ename.getText().toString(),taken,returned,Uri.parse(data[2]),"update",data[4]);
                        else
                            interact.save(data[5],Ename.getText().toString(),taken,returned,null,"update",data[4]);
                    }
                }
            });
            if (data[2]!=null)
                Eimage.setBackground(ob);

            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(tw,th));

            if (data[3].equals("")==false)
                Ename.setText(data[3]);                                                                    //adds data in card

            if (data[0].equals(String.valueOf(1)))
                taken.setChecked(true);
            else
                taken.setChecked(false);
            if (data[1].equals(String.valueOf(1)))
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
            if (data[7].equals("last"))
            {
                if ((hometime==null)&&(hoteltime==null)) {
                    Toast schreminder = Toast.makeText(context, "Add Schedule for notification support (from top).", Toast.LENGTH_LONG);
                    schreminder.show();
                }
                if (specialid==null)
                {
                    interact.save(data[5],"#%",new CheckBox(context),new CheckBox(context),null,"main","0");
                }
            }
        }
        protected void onCancelled(String data[])
        {
            notes.setText(data[3].substring(2));
            final String myFormat = "h:mm a, EEE, d MMM yyyy";
            sdf = new SimpleDateFormat(myFormat);
            if (data[0].equals("0")==false)
            {
                Calendar calendartemp = Calendar.getInstance();
                calendartemp.setTimeInMillis(Long.parseLong(data[0]));
                hometime = sdf.format(calendartemp.getTime());
            }
            if (data[1].equals("0")==false)
            {
                Calendar calendartemp = Calendar.getInstance();
                calendartemp.setTimeInMillis(Long.parseLong(data[1]));
                hoteltime = sdf.format(calendartemp.getTime());
            }
        }

    }
}