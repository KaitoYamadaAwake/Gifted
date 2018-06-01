package jp.co.abs.filedownloaderyamada;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;



public class MainActivity extends Activity {
    private static final int RESULT_PICK_IMAGEFILE = 1000;
    EditText editText;
     ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView =(ImageView)findViewById(R.id.URLView);
        registerForContextMenu(imageView);
        Button button = this.findViewById(R.id.URLGet);
        Button button2 = this.findViewById(R.id.open);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                        editText = findViewById(R.id.URL);
                        String URLText = editText.getText().toString();
                        Log.d(String.valueOf(editText), "a");

                        new AsyncTask<String, Void, Bitmap>() {
                            @Override
                            protected Bitmap doInBackground(String... strings) {
                                Bitmap image = null;
                                BitmapFactory.Options options;
                                try {
                                    URL url = new URL(strings[0]);
                                    options = new BitmapFactory.Options();
                                    options.inJustDecodeBounds = false;
                                    InputStream is = (InputStream) url.getContent();
                                    image = BitmapFactory.decodeStream(is, null, options);
                                    is.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return image;
                            }

                            @Override
                            protected void onPostExecute(Bitmap image) {
                                super.onPostExecute(image);

                                if (image != null) {
                                    ImageView imageView = (ImageView) MainActivity.this.findViewById(R.id.URLView);
                                    imageView.setImageBitmap(image);
                                    saveImage();
                                    Toast toast = Toast.makeText(MainActivity.this, "ダウンロードに成功しました。", Toast.LENGTH_LONG);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(MainActivity.this, "ダウンロードに失敗しました。", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }.execute(URLText);

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        imageView = (ImageView) findViewById(R.id.URLView);
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void saveImage(){
        imageView = (ImageView)findViewById(R.id.URLView);
        Bitmap Viewimage = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        ImageManager imageManager = new ImageManager(this);
        try {
            String albumName = "Yamadapic";
            imageManager.save(Viewimage,albumName);
        }catch (Error e){
            Log.e("MainActivity","onCreate:"+e);

            Toast.makeText(MainActivity.this,"保存できませんでした",Toast.LENGTH_SHORT).show();
        }finally {
            Toast.makeText(MainActivity.this,"保存できました",Toast.LENGTH_SHORT).show();
        }
    }
    static final int Twitter_Con = 0;
    static final int Gmail_Con = 1;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view ,ContextMenu.ContextMenuInfo MenuInfo){
        super.onCreateContextMenu(menu,view,MenuInfo);

        menu.setHeaderTitle("メニュータイトル");
        menu.add(0,Twitter_Con,0,"Twitter");
        menu.add(0,Gmail_Con,0,"Gmail");
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        switch (item.getItemId()){
            case Twitter_Con:
                return true;
            case Gmail_Con:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    }


