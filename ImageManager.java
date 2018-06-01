package jp.co.abs.filedownloaderyamada;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ImageManager {
    private Context mContext;
    private String fileFullPath;

    public ImageManager(Context context) {
        mContext = context;
    }


    public void save(Bitmap bitmap, String albumName) {
        saveToSd(getSdStorageDir(albumName), bitmap);
    }

    private void saveToSd(File dir, Bitmap bitmap) {
        String fileName = getFileName();
        fileFullPath = dir.getAbsolutePath() + "/" + fileName;
        try {
            FileOutputStream fos = new FileOutputStream(fileFullPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("Error", "" + e.toString());
        } finally {
            addGallery(fileName);
        }
    }


    private void addGallery(String fileName) {
        try {
            ContentValues values = new ContentValues();
            ContentResolver contentResolver = mContext.getContentResolver();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put("_data", fileFullPath);
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            Log.e("Error", "" + e);
        }
    }

    private String getFileName() {
        Date mDate = new Date();
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        String fileName = fileNameFormat.format(mDate) + ".jpg";

        return fileName;
    }


    private File getSdStorageDir(String albumName) {
        String extStrageDir = Environment.getExternalStorageDirectory().getPath();
        File dir = new File(extStrageDir, albumName);
        Log.d("dir", "" + dir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e("Error", "Directory not created");
            }
        }

        return dir;
    }
}