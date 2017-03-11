package lcukerd.com.stufflist;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private NestedScrollView nestedScrollView;
    private Button events;
    private String NameofEvents[];
    private String newEntry;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private LinearLayout linearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void onStart()
    {
        super.onStart();
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.startScroll);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        readfromDB();

        nestedScrollView.addView(linearLayout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            setName();
            return true;
        }
        else
        return super.onOptionsItemSelected(item);
    }

    public void setName()
    {
        AlertDialog.Builder eventName = new AlertDialog.Builder(this);
        eventName.setView(R.layout.dialog_add_name);
        final AlertDialog dialog = eventName.create();
        dialog.show();

        Button okay =(Button) dialog.findViewById(R.id.okay),cancel = (Button) dialog.findViewById(R.id.cancel);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameOfEvent = (EditText) dialog.findViewById(R.id.eventName);
                newEntry = nameOfEvent.getText().toString();
                dialog.dismiss();
                Intent addItem = new Intent(getApplicationContext(),addItem.class);
                addItem.putExtra("eventName",newEntry);
                startActivity(addItem);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    void readfromDB()
    {
        SQLiteDatabase db = dBcontract.getReadableDatabase();
        String[] projection = {
                eventDBcontract.ListofItem.columnID,
                eventDBcontract.ListofItem.columnEvent,
                eventDBcontract.ListofItem.columnName,
                eventDBcontract.ListofItem.columntaken,
                eventDBcontract.ListofItem.columnreturn,
                eventDBcontract.ListofItem.columnFileloc
        };

        Cursor cursor = db.query(eventDBcontract.ListofItem.tableName,projection,null,null,null,null,null);
        while(cursor.moveToNext())
        {
            Log.d("column return",cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnID))+" "+cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent))+" "+cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnName))+" "+cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columntaken))+" "+cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnreturn))+" "+cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnFileloc)));
        }
        cursor = db.query(eventDBcontract.ListofItem.tableName,projection,null,null,eventDBcontract.ListofItem.columnEvent,null,null);
        int i=0;
        NameofEvents = new String[cursor.getCount()];
        while(cursor.moveToNext())
        {
            NameofEvents[i] = cursor.getString(cursor.getColumnIndex(eventDBcontract.ListofItem.columnEvent));
            i++;
        }
        UpdateScrollView();

    }
    private void UpdateScrollView()
    {
        for (int i=0;i<NameofEvents.length;i++)
        {
            events = new Button(this);
            events.setGravity(View.TEXT_DIRECTION_LTR);
            events.setText(NameofEvents[i]);
            linearLayout.addView(events);
            final int ch=i;
            events.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),showList.class);
                    intent.putExtra("Event_Name",NameofEvents[ch]);
                    startActivity(intent);
                }
            });
        }
    }


}
