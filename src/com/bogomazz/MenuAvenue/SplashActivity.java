package com.bogomazz.MenuAvenue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import com.bogomazz.MenuAvenue.MainPane.MainActivity;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.bogomazz.MenuAvenue.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andrey on 11/14/14.
 */
public class SplashActivity extends Activity implements DataLoader.OnDataLoaded {
    SharedPreferences sharedPreferences;
    Button loginButton;
    EditText email;
    EditText password;
    TextView skip;
    ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_screen);

        setUpUI();
        try {
            new DataLoader(this).execute();
        } catch (Exception ex) {

            Log.d("Exception", ex.toString());
        }

    }

    private void setUpUI() {
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        ((TextView)findViewById(R.id.signUpLabel)).setTypeface(titleFont);

        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        User.email = (String) sharedPreferences.getString("email", null);
        User.password = (String) sharedPreferences.getString("password", null);
        User.userId = (String) sharedPreferences.getString("userId", null);
        if ( User.email == null ) {
            findViewById(R.id.loginForm).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar2).setVisibility(View.GONE);
            email = (EditText) findViewById(R.id.email);
            password = (EditText) findViewById(R.id.password);
            skip = ((TextView) findViewById(R.id.skip));
            skip.setVisibility(View.GONE);
            loginButton = (Button) findViewById(R.id.login);
            loginButton.setTypeface(titleFont);
        }
    }

    public void onDataLoadComplete() {
        if ( User.email == null ) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar = ((ProgressBar) findViewById(R.id.progressBar));
                    progressBar.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                    skip.setVisibility(View.VISIBLE);
                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            String emailStr = email.getText().toString();
                            String passwordStr = password.getText().toString();
                            if (isEmail(emailStr)) {
                                User.email = emailStr;
                                sharedPreferences.edit().putString("email", emailStr).commit();
                            } else {
                                email.setText("");
                                email.setBackgroundColor(getResources().getColor(R.color.orange));
                                return;
                            }
                            if ( passwordStr.length() >= 8 && passwordStr.length() <= 20) {
                                User.password = passwordStr;
                                sharedPreferences.edit().putString("password", passwordStr).commit();
                            } else {
                                password.setText("");
                                password.setHint(getResources().getString(R.string.passwordLimit));
                                password.setBackgroundColor(getResources().getColor(R.color.orange));
                                return;
                            }
                            User.phone = getPhoneNumber();
                            if (User.phone == null) {
                                askForPhoneDialog();
                            }


                            if (User.phone != null) {
                                signUp();
                                intentMainActivity();
                            }

                        }
                    });
                    skip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            intentMainActivity();
                        }
                    });

                }
            });
        } else {

            intentMainActivity();
        }


    }
    public void noInternetConnection() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.internet_exception), Toast.LENGTH_LONG);
            }
        });
//        Toast toast = Toast.makeText(this, getResources().getText(R.string.internet_exception), Toast.LENGTH_LONG);
//        toast.show();
        if (Item.isDataLoaded) {
            intentMainActivity();
        }
    }

    private boolean isEmail(String email) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(email);

        return m.matches();
    }

    private void intentMainActivity() {
        Intent main = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(main);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Log.e("Exception", e.toString());
        }

        finish();
    }

    private void signUp() {
        String url = "http://bogomazz.com/flask/menuavenue/signUp/?email=" + User.email + "&password=" + User.password;
        url += "&phone=" + User.phone;
        try {
            new SignUp().execute(url).get();
        } catch (Exception ex) {
            Log.d("Exception", ex.toString());
        }

    }



    class SignUp extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... urls) {
            String response = null;
            try {
                ByteArrayOutputStream outputStream = DatabaseLoader.process_request(urls[0]);
                response = outputStream.toString();
            } catch (NoNetworkException e) {
                Log.e("NoNetworkException", urls[0].toString()+ " " + e.toString());
                noInternetConnection();
                Log.d(e.toString(), "No internet");
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return response;
        }

        protected void onPostExecute(String response) {
            if (response.equals("wrong password")) {
                password.setText("");
                password.setHint(getResources().getString(R.string.wrongPassword));
                password.setHintTextColor(getResources().getColor(R.color.pinkorange));
                password.setBackgroundColor(getResources().getColor(R.color.orange));
                progressBar.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
            } else {
                sharedPreferences.edit().putString("userId", response);
                sharedPreferences.edit().commit();
//                Log.e("userId", sharedPreferences.getString("userId", "Ooops!"));
                User.userId = response;
            }
        }
    }
    private String getPhoneNumber() {
        TelephonyManager phoneManager = (TelephonyManager)
                getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = phoneManager.getLine1Number();
        return phoneNumber;
    }

    private void askForPhoneDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Ваш номер телефона");
        alert.setMessage("Последний шаг!");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);

        alert.setPositiveButton("Завершить!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String phoneNumber = input.getText().toString();

                if (phoneNumber.length() < 10 || phoneNumber.length() > 13) {
                    input.setText("");
                    input.setHint("Введите украинский номер телефона.");
                    return;
                } else {
                    User.phone = phoneNumber;
                    signUp();
                    intentMainActivity();
                }

            }
        });

        alert.setNegativeButton("Отмена.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }


}
