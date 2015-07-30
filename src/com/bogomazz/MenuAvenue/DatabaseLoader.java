package com.bogomazz.MenuAvenue;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by andrey on 10/16/14.
 */
public class DatabaseLoader {
    private static int TIME_OUT_CONNECTION = 2000;
    private static int TIME_OUT_SOCKET = 2000;
    private Context mContext;



    public DatabaseLoader(Context context) {
        mContext = context;
    }

    public void run() throws NoNetworkException {
        if (DataLoader.isNetworkAvailable(mContext)) {
            if (!isUpdated()) {
                downloadDatabase();
            }
        } else {
            throw new NoNetworkException();
        }
    }

    private boolean isUpdated() throws NoNetworkException {
        String urlIsUpdateNeeded = "http://bogomazz.com/flask/menuavenue/db?version=" + Database.VERSION;
        ByteArrayOutputStream response = process_request(urlIsUpdateNeeded);
        Log.e("response", response.toString());
        if (response.toString().equals("0")) {
            return true;
        }
        Database.VERSION = Double.parseDouble(response.toString());
        return false;
    }

    private void downloadDatabase() throws NoNetworkException {
        String urlGetDatabase = "http://bogomazz.com/flask/menuavenue/db";
        ByteArrayOutputStream response = process_request(urlGetDatabase);

        Log.e("download database", response.toString());
        saveDatabase(response);
        try {
            response.close();
        } catch (Exception e) {
            Log.e("Exception in download output stream", e.toString());
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    public static ByteArrayOutputStream process_request(String url) throws NoNetworkException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIME_OUT_CONNECTION);
            HttpConnectionParams.setSoTimeout(httpParameters, TIME_OUT_SOCKET);
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            HttpResponse request = httpclient.execute(new HttpGet(url));
            StatusLine statusLine = request.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                request.getEntity().writeTo(outputStream);
                outputStream.close();
            } else {
                //Closes the connection.
                request.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            Log.e(e.toString(), "Some exception "+ url);
            throw new NoNetworkException();
        }
        return outputStream;
    }

    private void saveDatabase(ByteArrayOutputStream bytesToSave) {
        String outFileName = Database.DATABASE_FILE.getAbsolutePath();
        File outFile = new File(outFileName);
        OutputStream fileOutput = null;

        if (!outFile.exists()) {
            outFile.getParentFile().mkdirs();
        } else {
            outFile.delete();
        }
        try {
            fileOutput = new FileOutputStream(outFileName);
            fileOutput.write(bytesToSave.toByteArray(), 0, bytesToSave.size());

            fileOutput.flush();
            fileOutput.close();
        } catch (Exception e) {
            Log.e("Save Database", e.toString());
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }





}
