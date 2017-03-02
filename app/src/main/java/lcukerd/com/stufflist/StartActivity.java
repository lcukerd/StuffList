package lcukerd.com.stufflist;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class StartActivity extends AppCompatActivity {

    private NestedScrollView nestedScrollView;
    private Button events;
    private String NameofEvents[] = {"first","second","third","fourth","fifth"};

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
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i=0;i<NameofEvents.length;i++)
        {
            events = new Button(this);
            events.setGravity(View.TEXT_DIRECTION_LTR);
            events.setText(NameofEvents[i]);
            linearLayout.addView(events);
            events.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // start next activity
                }
            });
        }
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
            AlertDialog.Builder eventName = new AlertDialog.Builder(this);
            eventName.setView(R.layout.dialog_add_name);
            AlertDialog dialog = eventName.create();
            dialog.show();
            return true;
        }
        else
        return super.onOptionsItemSelected(item);
    }
}
