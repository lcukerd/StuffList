package lcukerd.com.stufflist;
/*
pic disappears after switching app.
add on screen button to go back
Remove image from gallery
image not saved if rotated while closing camera
clicking on delete, deletes image before pressing on update.
*/

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.widget.RelativeLayout.ALIGN_BASELINE;
import static android.widget.RelativeLayout.ALIGN_BOTTOM;
import static android.widget.RelativeLayout.END_OF;

public class addItem extends AppCompatActivity {

    private EditText Sname;
    private ImageButton Simage;
    private CheckBox taken , returned ;
    private Button finish , more ;

    private Bitmap photo=null;
    private static final int CAMERA_REQUEST = 1004;
    private static final int SELECT_FILE = 1005;
    private Context context = this;
    private Uri photoURI,photoURIc;
    private Boolean updateImage = false , galleryused = false , deleteimageupdate = false , calledbylist =false;
    private String eventName;
    private Boolean camerastarted = false , showpopup = false;
    private String ItemName;
    private int t,r;
    private File photoFile;
    private String caller;
    private Intent add;
    private String id;
    private String info[] = new String[5];
    private DBinteract interact = new DBinteract(this);
    private eventDBcontract dBcontract = new eventDBcontract(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        add = getIntent();
        eventName = add.getStringExtra("eventName");
        caller = add.getStringExtra("calledby");
        String nameOfevents[] = interact.readfromDB(eventDBcontract.ListofItem.columndatetime+" ASC");
        try
        {if (nameOfevents[0].equals("Titorizl")) {
            SQLiteDatabase db = dBcontract.getWritableDatabase();
            Log.d("delete operation",String.valueOf(db.delete(eventDBcontract.ListofItem.tableName,eventDBcontract.ListofItem.columnEvent+" = "+"'"+nameOfevents[0]+"'",null)));
            Intent tutorial = new Intent(this, showPic.class);
            tutorial.putExtra("photo uri", "tutorial");
            startActivity(tutorial);
        }}
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void onStart()                                                                        //Delete photoUri in ondestroy if not clicked on either button
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
            info = add.getStringArrayExtra("available data");
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
                    showpopup = true;
                    calledbylist = true;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            id = info[4];
            ViewGroup layout =(ViewGroup) finish.getParent();
            layout.removeView(finish);
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            param.addRule(END_OF,R.id.back);
            param.addRule(ALIGN_BASELINE,R.id.back);
            param.addRule(ALIGN_BOTTOM,R.id.back);
            Sname.setLayoutParams(param);
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

                showdialogselector();

            }
        });
        Simage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ((photoURI!=null)||(showpopup==true)) {
                    try {
                        Log.d("Long Click", "successful");
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.popup, (ViewGroup) findViewById(R.id.pop));
                        PopupWindow pw = new PopupWindow(layout, 400, 200, true);
                        int coord[] = new int[2];
                        v.getLocationOnScreen(coord);
                        pw.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent)));
                        pw.setOutsideTouchable(true);
                        pw.showAtLocation(v, Gravity.NO_GRAVITY, coord[0] + 50, coord[1] + 100);
                        Button del = (Button) layout.findViewById(R.id.del);
                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (showpopup == true) {
                                    if (info[3].charAt(0)!='a')
                                        deleteimage(info[3]);
                                    else
                                        info[3] = null;
                                    showpopup = false ;
                                } else
                                {
                                    if (photoURI.toString().charAt(0)!='a')
                                        deleteimage(photoURI.toString());
                                    else
                                        photoURI = null;
                                }

                                recreate();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }return true;
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.save(eventName,Sname.getText().toString(),taken,returned,photoURI,caller,id);
                camerastarted=false;
                finish();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((photoURI==null)&&(caller.equals("list")))
                    interact.save(eventName,Sname.getText().toString(),taken,returned,Uri.parse(info[3]),caller,id);
                else
                    interact.save(eventName,Sname.getText().toString(),taken,returned,photoURI,caller,id);
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

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if (requestCode==CAMERA_REQUEST)
        {
            if (resultCode== Activity.RESULT_OK)
            {
                Log.d("Camera call","opened with result");
                if (photoURI!=null)
                    deleteimage(photoURI.toString());
                photoURI = photoURIc;
                updateImage = true;
            }
            else {
                Log.e("Camera call", "Failed");
                updateImage = true;
            }
        }
        else if (requestCode==SELECT_FILE)
        {
            if (resultCode== Activity.RESULT_OK)
            {

                if (photoURI!=null)
                    deleteimage(photoURI.toString());
                photoURI = data.getData();
                Log.d("Gallery call","opened with result " + photoURI.toString());
                updateImage=true;
                galleryused = true;
            }
            else {
                Log.e("Gallery call", "Failed");
                updateImage = true;
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
                /*if (photo.getWidth()<photo.getHeight())
                    photo = Bitmap.createScaledBitmap(photo,720,1280,false);
                else
                    photo = Bitmap.createScaledBitmap(photo,1280,720,false);
                */
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                if (photo.getHeight()>photo.getWidth())
                    photo = Bitmap.createScaledBitmap(photo,metrics.widthPixels/2,metrics.heightPixels/2,false);
                else
                    photo = Bitmap.createScaledBitmap(photo,metrics.heightPixels/2,metrics.widthPixels/2,false);

                if (galleryused = false) {
                    photoFile.delete();
                }
                    FileOutputStream out = null;
                    try {
                        File image = createImageFile();
                        photoURI = FileProvider.getUriForFile(context, "lcukerd.com.android.fileprovider", image);
                        out = new FileOutputStream(image);
                        deleteimageupdate = false;
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
            galleryused = false;
        }


    }

    private void deleteimage(String imageloc)
    {
        ContentResolver imagefile = getContentResolver();
        Log.d("file deletion",String.valueOf(imagefile.delete(Uri.parse(imageloc),null,null))+" "+imageloc);
    }

    private File createImageFile() throws IOException
    {
        String EName = "Stuff";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(EName,".jpg",storageDir);
        return image;
    }

    private void showdialogselector()
    {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};
        AlertDialog.Builder choose_cap_opt = new AlertDialog.Builder(this);
        choose_cap_opt.setTitle("Choose where to add from");
        choose_cap_opt.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
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
                            photoURIc = FileProvider.getUriForFile(context,"lcukerd.com.android.fileprovider",photoFile);
                            startCamera.putExtra(MediaStore.EXTRA_OUTPUT,photoURIc);
                            camerastarted=true;
                            startActivityForResult(startCamera,CAMERA_REQUEST);
                        }
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }

            }
        });
        choose_cap_opt.show();
    }

}
