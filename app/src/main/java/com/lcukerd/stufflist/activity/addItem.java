package com.lcukerd.stufflist.activity;
/*
Old Image not deleted
pic disappears after switching app.
add on screen button to go back
Remove image from gallery
image not saved if rotated while closing camera
*/

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lcukerd.stufflist.R;
import com.lcukerd.stufflist.database.DBinteract;
import com.lcukerd.stufflist.database.eventDBcontract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class addItem extends AppCompatActivity
{

    private EditText Sname;
    private ImageButton Simage;
    private MenuItem taken, returned;
    private static final String tag = addItem.class.getSimpleName();

    private static final int CAMERA_REQUEST = 1004;
    private static final int SELECT_FILE = 1005;
    private Context context = this;
    private Bitmap photo = null;
    private Uri photoURI, photoURIc;
    private Boolean updateImage = false, galleryused = false, camerastarted = false, showpopup = false;
    private String eventName, ItemName, caller, id, info[] = new String[5];
    private int t, r;
    private File photoFile;
    private Intent add;
    private DBinteract interact = new DBinteract(this);
    private eventDBcontract dBcontract = new eventDBcontract(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        add = getIntent();
        eventName = add.getStringExtra("eventName");
        caller = add.getStringExtra("calledby");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_add_item);

        if (!caller.equals("list"))
            if (preferences.getBoolean("intialLaunchAdd", true))
            {
            /*Intent tutorial = new Intent(this, showPic.class);
            tutorial.putExtra("photo uri", "tutorial");
            startActivity(tutorial);*/
                findViewById(R.id.instruction1).setVisibility(View.VISIBLE);
                findViewById(R.id.instruction2).setVisibility(View.VISIBLE);
                Toast delpic = Toast.makeText(this, "Some phones save copy of image taken from app in gallery, You can safely delete it from there. ", Toast.LENGTH_LONG);
                delpic.show();
                preferences.edit().putBoolean("intialLaunchAdd", false).commit();
            }

        Toolbar toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                item.setChecked(!item.isChecked());
                return true;
            }
        });
        toolbarBottom.inflateMenu(R.menu.menu_add_bottom);

        Sname = (EditText) findViewById(R.id.Sname);
        Simage = (ImageButton) findViewById(R.id.Simage);
        taken = toolbarBottom.getMenu().findItem(R.id.item_taken);
        returned = toolbarBottom.getMenu().findItem(R.id.item_back);

        CreateView();
    }

    private void CreateView()
    {
        Sname.setText("");
        Simage.setImageBitmap(null);
        taken.setChecked(false);
        returned.setChecked(false);

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
            if (info[3] != null)
            {
                try
                {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(info[3]));
                    Simage.setImageBitmap(photo);
                    showpopup = true;
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            id = info[4];

        }
        Simage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ItemName = Sname.getText().toString();
                if (taken.isChecked())
                    t = 1;
                else
                    t = 0;
                if (returned.isChecked())
                    r = 1;
                else
                    r = 0;

                showdialogselector();

            }
        });
        Simage.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if ((photoURI != null) || (showpopup == true))
                {
                    try
                    {
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
                        del.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (showpopup == true)
                                {
                                    if (info[3].charAt(0) != 'a')
                                        deleteimage(info[3]);
                                    info[3] = null;
                                    showpopup = false;
                                } else
                                {
                                    if (photoURI.toString().charAt(0) != 'a')
                                        deleteimage(photoURI.toString());
                                    photoURI = null;
                                }

                                CreateView();
                            }
                        });

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        if (camerastarted == true)
            onWindowFocusChanged(true);

    }

    protected void onDestroy()
    {
        super.onDestroy();
        if (photoURI != null)
        {
            deleteimage(photoURI.toString());
            Log.d("Destroy", "Deleteing extra image");
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Log.d("Camera call", "opened with result");
                if (photoURI != null)
                    deleteimage(photoURI.toString());
                photoURI = photoURIc;
                updateImage = true;
            } else
            {
                Log.e("Camera call", "Failed");
                updateImage = true;
            }
        } else if (requestCode == SELECT_FILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {

                if (photoURI != null)
                    deleteimage(photoURI.toString());
                photoURI = data.getData();
                Log.d("Gallery call", "opened with result " + photoURI.toString());
                updateImage = true;
                galleryused = true;
            } else
            {
                Log.e("Gallery call", "Failed");
                updateImage = true;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if ((updateImage == true) && (photoURI != null))
        {
            try
            {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                photo = Bitmap.createScaledBitmap(photo, (2 * photo.getWidth()) / 3, (2 * photo.getHeight()) / 3, true);
                saveandchangePic();
            } catch (IOException e)
            {
                Log.e(tag, "Error reading uri", e);
            }
        }
    }

    private void saveandchangePic()
    {
        try
        {
            if (galleryused == false && photoFile != null)
            {
                photoFile.delete();
            }
            new Thread(new Runnable()
            {
                public void run()
                {
                    FileOutputStream out = null;
                    try
                    {
                        File image = createImageFile();
                        photoURI = FileProvider.getUriForFile(context, "lcukerd.com.android.fileprovider", image);
                        out = new FileOutputStream(image);
                        photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        try
                        {
                            if (out != null)
                            {
                                out.close();
                            }
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            Simage.setImageBitmap(photo);
            Sname.setText(ItemName);
            if (t == 1)
                taken.setChecked(true);
            else
                taken.setChecked(false);
            if (r == 1)
                returned.setChecked(true);
            else
                returned.setChecked(false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        updateImage = false;
        galleryused = false;
    }


    private void deleteimage(String imageloc)
    {
        ContentResolver imagefile = getContentResolver();
        Log.d("file deletion", String.valueOf(imagefile.delete(Uri.parse(imageloc), null, null)) + " " + imageloc);
    }

    private File createImageFile() throws IOException
    {
        String EName = "Stuff";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(EName, ".jpg", storageDir);
        return image;
    }

    private void showdialogselector()
    {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};
        AlertDialog.Builder choose_cap_opt = new AlertDialog.Builder(this);
        choose_cap_opt.setTitle("Choose where to add from");
        choose_cap_opt.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int item)
            {

                if (items[item].equals("Take Photo"))
                {
                    Intent startCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (startCamera.resolveActivity(getPackageManager()) != null)
                    {
                        photoFile = null;
                        try
                        {
                            photoFile = createImageFile();
                        } catch (IOException ex)
                        {
                            Log.e("File Creation", "error");
                        }
                        if (photoFile != null)
                        {
                            photoURIc = FileProvider.getUriForFile(context, "com.lcukerd.android.fileprovider", photoFile);
                            startCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURIc);
                            camerastarted = true;
                            startActivityForResult(startCamera, CAMERA_REQUEST);
                        }
                    }
                } else if (items[item].equals("Choose from Library"))
                {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, SELECT_FILE);
                }

            }
        });
        choose_cap_opt.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        if (caller.equals("list"))
        {
            menu.findItem(R.id.action_done).setTitle("update")
                    .setIcon(R.drawable.ic_save_white_24dp);
            menu.findItem(R.id.action_done_all).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int menuid = item.getItemId();
        if (menuid == R.id.action_done)
        {
            if ((photoURI == null) && (caller.equals("list")) && (info[3] != null))
                interact.save(eventName, Sname.getText().toString(), taken, returned, Uri.parse(info[3]), caller, id);
            else if ((photoURI == null) && (caller.equals("list")))
                interact.save(eventName, Sname.getText().toString(), taken, returned, null, caller, id);
            else
                interact.save(eventName, Sname.getText().toString(), taken, returned, photoURI, caller, id);
            photoURI = null;
            camerastarted = false;
            Sname.setText("");
            taken.setChecked(false);
            returned.setChecked(false);
            if (caller.equals("main"))
                CreateView();
            else
            {
                showList.recreateActivity = true;
                finish();
            }
            Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show();
        } else if (menuid == R.id.action_done_all)
        {
            interact.save(eventName, Sname.getText().toString(), taken, returned, photoURI, caller, id);
            photoURI = null;
            camerastarted = false;
            showList.recreateActivity = true;
            Toast.makeText(context, "Item added", Toast.LENGTH_SHORT).show();
            finish();
        } else if (menuid == R.id.action_rotate)
        {
            if (photoURI != null)
                deleteimage(photoURI.toString());
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
            saveandchangePic();
        }
        findViewById(R.id.instruction1).setVisibility(View.GONE);
        findViewById(R.id.instruction2).setVisibility(View.GONE);
        return super.onOptionsItemSelected(item);
    }
}
