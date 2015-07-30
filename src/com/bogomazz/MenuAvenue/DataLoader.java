package com.bogomazz.MenuAvenue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import com.bogomazz.MenuAvenue.MainPane.MainActivity;
import com.bogomazz.MenuAvenue.Menu.Menu;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by andrey on 11/14/14.
 */
public class DataLoader extends AsyncTask<Void, Void, Void> {
    private boolean isInternetConnectionExist = true;
    private Context mContext;
    private OnDataLoaded mTaskData;
    public static String urlImage = "http://bogomazz.com/flask/menuavenue/image/%s?density=-mdpi";
    public static String listOfImageNames[];

    public interface OnDataLoaded{
        void onDataLoadComplete();
        void noInternetConnection();
    }
    public DataLoader (SplashActivity activity){
        mContext = activity.getApplicationContext();
        mTaskData = activity;

    }

    public static void loadImageView(ImageView imageView, String imageName) {
        String url = String.format(urlImage, imageName);
        Log.d(url, "URL");
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    public void initImageLoader() {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
//        urlImage += getSreenDensity();
        File cacheDir = mContext.getFilesDir();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.logo_menuavenue)
                .showImageOnFail(R.drawable.logo_menuavenue)
                .resetViewBeforeLoading(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .diskCacheSize(100 * 1024 * 1024) // 50 Mb
                .diskCacheFileCount(150)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(options)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    protected Void doInBackground(Void... nothing) {
        initImageLoader();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Item.isIsDataLoadedOneTime = sharedPreferences.getBoolean("isLoadedOneTime", false);
        Log.d(Boolean.toString(Item.isIsDataLoadedOneTime), "DataLoadedOneTime");
        try {
            loadDatabase();
        } catch (NoNetworkException e) {
            isInternetConnectionExist = false;
        }
        if ( !isInternetConnectionExist ) {
            if (Item.isIsDataLoadedOneTime) {
                loadData();
                Item.isDataLoaded = true;
            } else {
                Item.isDataLoaded = false;
            }
        } else {
            loadData();
            if (!Item.isIsDataLoadedOneTime) {
                sharedPreferences.edit().putBoolean("isLoadedOneTime", true).commit();
                Item.isIsDataLoadedOneTime = true;
                Log.d("DataLoadedOneTime", Boolean.toString(Item.isIsDataLoadedOneTime));
            }
            Item.isDataLoaded = true;
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        if ( !isInternetConnectionExist ) {
            mTaskData.noInternetConnection();
        } else {
            mTaskData.onDataLoadComplete();
        }
    }

    private void loadDatabase() throws NoNetworkException{
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("pref", Context.MODE_PRIVATE);
        Database.VERSION = (double) sharedPreferences.getFloat("database_version", (float)Database.VERSION);
        double oldDatabaseVersion = Database.VERSION;

        Database.load(mContext);
//        if (oldDatabaseVersion != Database.VERSION) {
//            downloadImages();
//        }
        sharedPreferences.edit().putFloat("database_version", (float) Database.VERSION);

    }
    private void loadData() {
        Item.init(new Database(mContext));
//        Item.initImageResourcesForAllItems(mContext);
    }

//    private void downloadImages() {
//        try {
//            String[] listOfImageNames = listOfImageNames();
//            Log.d("First imageName", listOfImageNames[0]);
//            for (String imageName: listOfImageNames)  {
//                if (!isImageExist(imageName)) {
//                    saveImage(downloadImage(imageName), imageName);
//                }
//            }
//
//
//        } catch (NoNetworkException e) {
//            Log.e("NoNetworkException", "NoNetworkException");
//        }
//    }
//
//    private ByteArrayOutputStream downloadImage(String imageName) {
//        ByteArrayOutputStream response = null;
//        try {
//            String imageURL = "http://bogomazz.com/flask/menuavenue/image/" + imageName + "?density=" + density;
//            response = DatabaseLoader.process_request(imageURL);
//        } catch (NoNetworkException e) {
//            Log.e("NoNetworkException", "NoNetworkException");
//        }
//        return response;
//    }



    private void saveImage(ByteArrayOutputStream imageToSave, String imageName) {
        File fileToWrite = new File(mContext.getFilesDir(), imageName);
        try {
            OutputStream fileOutput = new FileOutputStream(fileToWrite);
            fileOutput.write(imageToSave.toByteArray(), 0, imageToSave.size());

            fileOutput.flush();
            fileOutput.close();
        } catch (Exception e) {
            Log.e("Save Database", e.toString());
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private boolean isImageExist(String imageName) {
        File file = new File(mContext.getFilesDir(), imageName);

        return file.exists();
    }

    private String[] listOfImageNames() throws NoNetworkException {
        String density = null;
        float densityFloat = mContext.getResources().getDisplayMetrics().density;
        if ( densityFloat <= 1.0 ) {
            density = "-mdpi";
        } else if ( densityFloat <= 1.5  ) {
            density = "-hdpi";
        } else {
            density = "-xhdpi";
        }
        String urlIsUpdateNeeded = "http://bogomazz.com/flask/menuavenue/getImageList?density=" + density;
        ByteArrayOutputStream response = DatabaseLoader.process_request(urlIsUpdateNeeded);

        String listOfImagesStr = response.toString();

        return listOfImagesStr.split("\n");
    }
    private String getSreenDensity() {
        String density = null;
        float densityFloat = mContext.getResources().getDisplayMetrics().density;
        if ( densityFloat <= 1.0 ) {
            density = "-mdpi";
        } else if ( densityFloat <= 1.5  ) {
            density = "-hdpi";
        } else {
            density = "-xhdpi";
        }
        return density;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else {
            return true;
        }
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
}
