package com.lcukerd.stufflist;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lcukerd.stufflist.database.DBinteract;
import com.lcukerd.stufflist.database.eventDBcontract;

public class MainActivity extends AppCompatActivity
{
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private LinearLayout linearLayout;
    private DBinteract interact = new DBinteract(this);
    private ContentValues values;
    private DisplayMetrics metrics;
    private EventListAdapter adapter;
    private SharedPreferences preferences;
    private static final String tag = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("intialLaunchG", true))
        {
            startActivity(new Intent(this,IntroActivity.class));
            preferences.edit().putBoolean("intialLaunchG", false).commit();
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setName();
            }
        });
        RecyclerView eventView = (RecyclerView) findViewById(R.id.recycler_event);
        String order = preferences.getString("pref_event_order",getString(R.string.defaultvae));
        adapter = new EventListAdapter(order, this);
        eventView.setLayoutManager(new LinearLayoutManager(this));
        eventView.setAdapter(adapter);

    }

    protected void onStart()
    {
        super.onStart();
        Log.d(tag,"onstart called");
        metrics = new DisplayMetrics();
        String order = preferences.getString("pref_event_order",getString(R.string.defaultvae));
        adapter.updateDataSet(order);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
            String itemnames[] = {"Headphone","Shoes","Sunglasses","Towel","Scarf","First aid kit","Charger","Laptop","Comb"};
            for (int i=0;i<9;i++) {
                values = new ContentValues();
                adddummyitem(itemnames[i], i % 2, i % 3, R.drawable.d1 + i);
                db.insert(eventDBcontract.ListofItem.tableName, null, values);
            }
            preferences.edit().putBoolean("intialLaunchAdd", true).commit();
            preferences.edit().putBoolean("intialLaunchG", true).commit();
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
        else if (id == R.id.log)
            startActivity(new Intent(this,updateLog.class));
        return super.onOptionsItemSelected(item);
    }

    public void adddummyitem(String name,int t,int r,int id)
    {
        values.put(eventDBcontract.ListofItem.columnEvent,"Sample");
        values.put(eventDBcontract.ListofItem.columnName,name);
        values.put(eventDBcontract.ListofItem.columndatetime,0);
        values.put(eventDBcontract.ListofItem.columntaken,String.valueOf(t));
        values.put(eventDBcontract.ListofItem.columnreturn,String.valueOf(r));
        Uri path = Uri.parse("android.resource://com.lcukerd.stufflist/" + id);
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

}
