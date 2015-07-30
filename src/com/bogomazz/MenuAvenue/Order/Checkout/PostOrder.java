package com.bogomazz.MenuAvenue.Order.Checkout;

import android.os.AsyncTask;
import android.util.Log;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by andrey on 12/23/14.
 */
public class PostOrder extends AsyncTask<JSONObject, Void, String> {
    private static final String URL_ADD_ORDER = "http://bogomazz.com/flask/menuavenue/order/";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(JSONObject... jsons) {
        try {
            return processPostRequest(URL_ADD_ORDER, jsons[0]);
//            Log.e(response, "response");
        } catch (NoNetworkException e) {

        }
        return null;
    }
    public String processPostRequest(String urlString, JSONObject jsonObject) throws NoNetworkException{
        String result = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlString);

        try {
            // Add your data
            Log.e(jsonObject.toString(), " json");
            Log.e(" json", jsonObject.toString());

            httppost.setEntity(new StringEntity(jsonObject.toString()));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = response.getEntity().getContent();
                result = convertStreamToString(inputStream);
            } else {
                //Closes the connection.
                Log.e(response.getStatusLine().getStatusCode()+"", "Bad request");
            }
        } catch (Exception e) {
            Log.e(e.toString(), "Some exception " + URL_ADD_ORDER);
            throw new NoNetworkException();
        }
        return result;
    }
    public String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected void onPostExecute(Boolean result) {

    }
}
