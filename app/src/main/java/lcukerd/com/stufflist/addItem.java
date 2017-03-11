package lcukerd.com.stufflist;
/*
pic disappears after switching app.
add on screen button to go back
text data in edittext disappears if clicked on add image after writing event name
Remove image from gallery
image not saved if rotated while closing camera
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class addItem extends AppCompatActivity {

    private EditText Sname;
    private ImageButton Simage;
    private CheckBox taken , returned ;
    private Button finish , more ;

    private Bitmap photo=null;
    private static final int CAMERA_REQUEST = 1004;
    private Context context = this;
    private Uri photoURI;
    private Boolean updateImage = false;
    private eventDBcontract dBcontract = new eventDBcontract(this);
    private String eventName;
    private Boolean camerastarted = false;
    private String ItemName;
    private int t,r;
    private File photoFile;
    private String caller;
    private Intent add;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        add = getIntent();
        eventName = add.getStringExtra("eventName");
        caller = add.getStringExtra("calledby");
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

        if (caller.equals("list"))
        {
            String info[] = add.getStringArrayExtra("available data");
            Sname.setText(info[0]);
            if (info[1].equals("1"))
                taken.setChecked(true);
            else
                taken.setChecked(false);
            if (info[2].equals("1"))
                returned.setChecked(true);
            else
                returned.setChecked(false);
            if (info[3]!=null)
            {
                try {
                    Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(info[3]));
                    Simage.setImageBitmap(photo);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            id = info[4];
            ViewGroup layout =(ViewGroup) finish.getParent();
            layout.removeView(finish);
            more.setText("Update");
        }


        Simage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemName = Sname.getText().toString();
                if (taken.isChecked())
                    t=1;
                else
                    t=0;
                if (returned.isChecked())
                    r=1;
                else
                    r=0;
                Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (startCamera.resolveActivity(getPackageManager())!=null)
                {
                   photoFile= null;
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
                        camerastarted=true;
                        startActivityForResult(startCamera,CAMERA_REQUEST);
                    }
                }

            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                camerastarted=false;
                finish();
                startActivity(new Intent(getApplicationContext(),StartActivity.class));
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();;
                camerastarted=false;
                Sname.setText("");
                taken.setChecked(false);
                returned.setChecked(false);
                if (caller.equals("main"))
                    recreate();
                else
                    finish();
            }
        });
        if (camerastarted==true)
            onWindowFocusChanged(true);

    }
    private void save()
    {
        SQLiteDatabase db = dBcontract.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(eventDBcontract.ListofItem.columnEvent,eventName);
        values.put(eventDBcontract.ListofItem.columnName,Sname.getText().toString());
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
        if (caller.equals("main"))
            db.insert(eventDBcontract.ListofItem.tableName,null,values);
        else
            db.update(eventDBcontract.ListofItem.tableName,values,"id=?",new String[]{id});
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
                photoFile.delete();
                File image = createImageFile();
                photoURI = FileProvider.getUriForFile(context,"lcukerd.com.android.fileprovider",image);
                if (photo.getWidth()<photo.getHeight())
                    photo = Bitmap.createScaledBitmap(photo,720,1280,false);
                else
                    photo = Bitmap.createScaledBitmap(photo,1280,720,false);
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(image);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Simage.setImageBitmap(photo);      // Image gets cropped look into it
                Sname.setText(ItemName);
                if (t==1)
                    taken.setChecked(true);
                else
                    taken.setChecked(false);
                if (r==1)
                    returned.setChecked(true);
                else
                    returned.setChecked(false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            updateImage = false;
        }


    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private File createImageFile() throws IOException
    {
        String EName = "Stuff";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(EName,".jpg",storageDir);
        return image;
    }

}
