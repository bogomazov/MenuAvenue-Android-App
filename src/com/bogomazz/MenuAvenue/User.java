package com.bogomazz.MenuAvenue;

import android.os.AsyncTask;
import android.util.Log;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;

import java.io.ByteArrayOutputStream;

/**
 * Created by andrey on 12/14/14.
 */
public class User {
    public static String email;
    public static String password;
    public static String userId;
    public static String phone;

    public static int CURRENT_LANGUAGE;
    public static final int LANUAGE_RUSSIAN = 0;
    public static final int LANUAGE_ENGLISH = 1;

    public static void initUser(String email, String phone) {
            try {
                String url = "http://bogomazz.com/flask/menuavenue/signUp/?email=" + email + "&password=" + "";
                Log.e("email and phone", email + " " + phone);
                url += "&phone=" + phone;
                String str_result = new SignUp().execute(url).get();
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
    }
    static class SignUp extends AsyncTask<String, Void, String> {


        protected void onPreExecute() {

        }

        protected String doInBackground(String... urls) {
            String response = null;
            try {
                ByteArrayOutputStream outputStream = DatabaseLoader.process_request(urls[0]);
                response = outputStream.toString();
                Log.e("response", response);
            } catch (NoNetworkException e) {
                Log.e("NoNetworkException", urls[0].toString()+ " " + e.toString());
                Log.d(e.toString(), "No internet");
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            User.userId = response;
            return response;
        }

    }
}
