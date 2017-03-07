package lcukerd.com.stufflist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
    private Bitmap photo=null;
    private static final int CAMERA_REQUEST = 1004;
    private String currentPhotoPath;
    private Context context = this;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void onStart()
    {
        super.onStart();
        setContentView(R.layout.activity_add_item);

        Sname = (EditText) findViewById(R.id.Sname);
        Simage = (ImageButton) findViewById(R.id.Simage);
        taken = (CheckBox) findViewById(R.id.taken);
        returned = (CheckBox) findViewById(R.id.back);

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

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if (requestCode==CAMERA_REQUEST)
        {
            if (resultCode== Activity.RESULT_OK)
            {
                Log.d("Camera call","opened with result");
                /*photo = (Bitmap) data.getExtras().get("data");
                Simage.setImageBitmap(photo);
                Simage.setImageURI(photoURI);
                */
            }
            else
                Log.e("Camera call","Failed");
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (photoURI!=null)
        {
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoURI.getPath(),options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            ViewGroup.LayoutParams params = Simage.getLayoutParams();
            params.width = 3*imageWidth/4;
            params.height = 3*imageHeight/4;
            try
            {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            Simage.setLayoutParams(params);
            */
            Simage.setImageURI(photoURI);                                                           // Image gets cropped look into it
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
