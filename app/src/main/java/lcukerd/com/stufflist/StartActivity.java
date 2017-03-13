package lcukerd.com.stufflist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.text.ParseException;
import java.util.Date;


public class StartActivity extends AppCompatActivity {

    private NestedScrollView nestedScrollView;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private LinearLayout linearLayout;
    private DBinteract interact = new DBinteract(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void onStart()
    {
        super.onStart();

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String order = preferences.getString("pref_event_order",getString(R.string.defaultvae));

        order = updateorder(order);

        setContentView(R.layout.activity_start);
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
            return super.onOptionsItemSelected(item);
    }

    public void setName()                                                                           // Open dialog box
    {
        AlertDialog.Builder eventName = new AlertDialog.Builder(this,R.style.dialogStyle);
        eventName.setView(R.layout.dialog_add_name);
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
            events.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        Log.d("Long Click","successful");
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup,(ViewGroup)findViewById(R.id.pop));
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
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }


}
