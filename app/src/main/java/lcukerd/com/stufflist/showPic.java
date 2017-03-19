package lcukerd.com.stufflist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class showPic extends AppCompatActivity {

    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_pic);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        String data = intent.getStringExtra("photo uri");
        pic = (ImageView) findViewById(R.id.imageView);
        try {

            Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(data));
            Log.d("Size of image", "width:"+photo.getWidth()+" height:"+photo.getHeight());
            /*if ((metrics.heightPixels>metrics.widthPixels)&&(photo.getHeight()<photo.getWidth()))
            {
                th =(int) ( tw* ( ((float)photo.getHeight()) / ((float)photo.getWidth()) ));
                Log.d("Metrics for landscape",String.valueOf(tw)+" "+String.valueOf(th));
            }*/
            pic.setImageBitmap(photo);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Couldn't Load",data);
        }
    }
}