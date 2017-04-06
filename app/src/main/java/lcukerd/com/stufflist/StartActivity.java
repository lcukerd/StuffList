package lcukerd.com.stufflist;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;



public class StartActivity extends AppCompatActivity {

    private NestedScrollView nestedScrollView;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private LinearLayout linearLayout;
    private DBinteract interact = new DBinteract(this);
    private ContentValues values;
    private DisplayMetrics metrics;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String nameOfevents[] = interact.readfromDB(eventDBcontract.ListofItem.columndatetime+" ASC");
        if (nameOfevents.length>0)
            if (nameOfevents[0].equals("Titorizl"))
                startActivity(new Intent(this,IntroActivity.class));

    }

    protected void onStart()
    {
        super.onStart();

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String order = preferences.getString("pref_event_order",getString(R.string.defaultvae));

        order = updateorder(order);

        setContentView(R.layout.activity_start);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setName();
            }
        });

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.startScroll);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        String nameOfevents[] = interact.readfromDB(order);
        UpdateScrollView(nameOfevents);
        nestedScrollView.addView(linearLayout);
    }

    public String updateorder(String order)
    {
        if (order.equals("date_asc_event"))
            order = eventDBcontract.ListofItem.columndatetime+" ASC";
        else if (order.equals("date_desc_event"))
            order = eventDBcontract.ListofItem.columndatetime+" DESC";
        else if (order.equals("name_asc_event"))
            order = eventDBcontract.ListofItem.columnEvent+" ASC";
        else if (order.equals("name_desc_event"))
            order = eventDBcontract.ListofItem.columnEvent+" DESC";
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
            setName();
            return true;
        }
        else if (id == R.id.action_settings)
        {
            startActivity(new Intent(this,orderevent.class));
            return true;
        }
        else if (id == R.id.action_tut)
        {
            SQLiteDatabase db = dBcontract.getWritableDatabase();
            values = new ContentValues();
            values.put(eventDBcontract.ListofItem.columnEvent,"Titorizl");
            db.insert(eventDBcontract.ListofItem.tableName,null,values);
            String itemnames[] = {"Headphone","Shoes","Sunglasses","Towel","Scarf","First aid kit","Charger","Laptop","Comb"};
            for (int i=0;i<9;i++) {
                values = new ContentValues();
                adddummyitem(itemnames[i], i % 2, i % 3, R.drawable.d1 + i);
                db.insert(eventDBcontract.ListofItem.tableName, null, values);
            }
            startActivity(new Intent(this,IntroActivity.class));
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
            return super.onOptionsItemSelected(item);
    }

    public void adddummyitem(String name,int t,int r,int id)
    {
        values.put(eventDBcontract.ListofItem.columnEvent,"Sample");
        values.put(eventDBcontract.ListofItem.columnName,name);
        values.put(eventDBcontract.ListofItem.columndatetime,0);
        values.put(eventDBcontract.ListofItem.columntaken,String.valueOf(t));
        values.put(eventDBcontract.ListofItem.columnreturn,String.valueOf(r));
        Uri path = Uri.parse("android.resource://lcukerd.com.stufflist/" + id);
        values.put(eventDBcontract.ListofItem.columnFileloc, path.toString());
    }

    public void setName()                                                                           // Open dialog box
    {
        AlertDialog.Builder eventName = new AlertDialog.Builder(this,R.style.dialogStyle);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View dialogb = inflater.inflate(R.layout.dialog_add_name, null);
        eventName.setView(dialogb);
        final AlertDialog dialog = eventName.create();
        dialog.show();
        final InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        Button okay =(Button) dialog.findViewById(R.id.okay),cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText nameOfEvent = (EditText) dialog.findViewById(R.id.eventName);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEntry = nameOfEvent.getText().toString();
                nameOfEvent.clearFocus();
                imm.hideSoftInputFromWindow(nameOfEvent.getWindowToken(), 0);
                dialog.dismiss();

                Intent addItem = new Intent(getApplicationContext(),addItem.class);
                addItem.putExtra("eventName",newEntry);
                addItem.putExtra("calledby","main");
                startActivity(addItem);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameOfEvent.clearFocus();
                imm.hideSoftInputFromWindow(nameOfEvent.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
    }

    private void UpdateScrollView(final String NameofEvents[])                                      // reads array of string to display list of events
    {
        Button events;
        for (int i=0;i<NameofEvents.length;i++) {
            if (NameofEvents[i].equals("Titorizl") == false) {
                events = new Button(this);

                events.setGravity(View.TEXT_DIRECTION_LTR);
                events.setText(NameofEvents[i]);
                linearLayout.addView(events);
                final int ch = i;
                events.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), showList.class);
                        intent.putExtra("Event_Name", NameofEvents[ch]);
                        //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                        startActivity(intent);
                    }
                });
                events.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        try {
                            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            View layout = inflater.inflate(R.layout.actionbuttons, (ViewGroup) findViewById(R.id.actionButtons));
                            Log.d("Popup",String.valueOf(metrics.widthPixels));
                            PopupWindow pw = new PopupWindow(layout, 350  * (metrics.widthPixels/1080), 200, true);
                            pw.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent)));
                            pw.setOutsideTouchable(true);
                            int coord[] = new int[2];
                            v.getLocationOnScreen(coord);

                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Fade explode = new Fade();
                                pw.setEnterTransition(explode);
                            }

                            pw.showAtLocation(v, Gravity.NO_GRAVITY, 500, coord[1] - 100);

                            Button add = (Button) layout.findViewById(R.id.popupadd);
                            Button del = (Button) layout.findViewById(R.id.popupdel);
                            add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent addItem = new Intent(getApplicationContext(), addItem.class);
                                    addItem.putExtra("eventName", NameofEvents[ch]);
                                    addItem.putExtra("calledby", "main");
                                    startActivity(addItem);
                                    recreate();
                                }
                            });
                            del.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SQLiteDatabase db = dBcontract.getWritableDatabase();
                                    Cursor cursor = interact.readinEvent(NameofEvents[ch], eventDBcontract.ListofItem.columndatetime + " ASC");
                                    while (cursor.moveToNext()) {
                                        String imageloc = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc));
                                        if (imageloc != null) {
                                            if (imageloc.charAt(0)!='a') {
                                                ContentResolver imagefile = getContentResolver();
                                                Log.d("file deletion", String.valueOf(imagefile.delete(Uri.parse(imageloc), null, null)) + " " + imageloc);
                                            }
                                        }
                                        Log.d("delete operaiton", String.valueOf(db.delete(eventDBcontract.ListofItem.tableName, eventDBcontract.ListofItem.columnID + " = " + cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID)), null)));
                                    }//Log.d("delete operation",String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,eventDBcontract.ListofItem.columnEvent+" = "+"'"+NameofEvents[ch]+"'",null)));
                                    recreate();                                                         //add option to delete files as well

                                }
                            });
                        /*
                        add.setLayoutParams(new GridLayout.LayoutParams(new ViewGroup.LayoutParams(75,75)));
                        del.setLayoutParams(new GridLayout.LayoutParams(new ViewGroup.LayoutParams(75,75)));
                        ex.setLayoutParams(new GridLayout.LayoutParams(new ViewGroup.LayoutParams(75,75)));*/

                        /*Log.d("Long Click","successful");
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup,(ViewGroup)findViewById(R.id.popl));
                        PopupWindow pw = new PopupWindow(layout, 400, 200, true);
                        int coord[]= new int[2];
                        v.getLocationOnScreen(coord);
                        pw.showAtLocation(v, Gravity.NO_GRAVITY, 700 ,coord[1]+100);
                        Button del = (Button) layout.findViewById(R.id.del);
                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteDatabase db = dBcontract.getWritableDatabase();
                                Log.d("delete operaiton",String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,eventDBcontract.ListofItem.columnEvent+" = "+"'"+NameofEvents[ch]+"'",null)));
                                recreate();                                                         //add option to delete files as well
                            }
                        });*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;

                    }
                });
            }
        }
    }


}
