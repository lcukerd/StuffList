package lcukerd.com.stufflist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class addItem extends AppCompatActivity {

    private EditText Sname;
    private ImageButton Simage;
    private CheckBox taken , returned ;
    private Button finish , more ;

    private Bitmap photo=null;
    private static final int CAMERA_REQUEST = 1004;
    private String currentPhotoPath;
    private Context context = this;
    private Uri photoURI;
    private Boolean updateImage = false;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent add = getIntent();
        eventName = add.getStringExtra("eventName");

    }
    protected void onStart()
    {
        super.onStart();
        setContentView(R.layout.activity_add_item);

        Sname = (EditText) findViewById(R.id.Sname);
        Simage = (ImageButton) findViewById(R.id.Simage);
        taken = (CheckBox) findViewById(R.id.taken);
        returned = (CheckBox) findViewById(R.id.back);
        finish = (Button) findViewById(R.id.finish);
        more = (Button) findViewById(R.id.more);

        Simage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (startCamera.resolveActivity(getPackageManager())!=null)
                {
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                    }
                    catch (IOException ex)
                    {
                        Log.e("File Creation","error");
                    }
                    if (photoFile!=null)
                    {
                        photoURI = FileProvider.getUriForFile(context,"lcukerd.com.android.fileprovider",photoFile);
                        startCamera.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                        startActivityForResult(startCamera,CAMERA_REQUEST);
                    }
                }

            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                startActivity(new Intent(getApplicationContext(),StartActivity.class));
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                Sname.setText("");
                taken.setChecked(false);
                returned.setChecked(false);
                recreate();
            }
        });

    }
    private void save()
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnEvent,eventName);
        values.put(eventDBcontract.ListofItem.columnName,Sname.getText().toString());               //not save null when nothing written instead saves nothing
        if (taken.isChecked()==true)
            values.put(eventDBcontract.ListofItem.columntaken,"1");
        else
            values.put(eventDBcontract.ListofItem.columntaken,"0");
        if (returned.isChecked()==true)
            values.put(eventDBcontract.ListofItem.columnreturn,"1");
        else
            values.put(eventDBcontract.ListofItem.columnreturn,"0");
        if (photoURI!=null) {
            values.put(eventDBcontract.ListofItem.columnFileloc, photoURI.toString());
            Log.d("File address write", photoURI.toString());
        }
        long newRowId = db.insert(eventDBcontract.ListofItem.tableName,null,values);
        Log.d("P.K of stuff",Long.toString(newRowId));

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if (requestCode==CAMERA_REQUEST)
        {
            if (resultCode== Activity.RESULT_OK)
            {
                Log.d("Camera call","opened with result");
                updateImage = true;
            }
            else {
                Log.e("Camera call", "Failed");
                photoURI = null;
            }
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if ((updateImage==true)&&(photoURI!=null))
        {
            try
            {

                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                Simage.setImageBitmap(Bitmap.createScaledBitmap(photo, Simage.getMeasuredWidth(), Simage.getMeasuredHeight(), false));      // Image gets cropped look into it
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            updateImage = false;
        }


    }

    private File createImageFile() throws IOException
    {
        String EName = "nothing for now";
        String itr = "1";                                   //remember to change this in case of conflicts
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(EName+"_"+itr,".jpg",storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
