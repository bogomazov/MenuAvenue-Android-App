package com.bogomazz.MenuAvenue;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;

import java.util.concurrent.ExecutionException;

/**
 * Created by andrey on 10/12/14.
 */
public class RootActivity extends ActionBarActivity {
    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        onStartCount = 1;

        if ( savedInstanceState == null ) {
            this.overridePendingTransition(R.animator.in_from_right,
                    R.animator.out_to_left);
        }

        checkDataInit();
    }

    @Override
    protected void onRestart() {
        super.onResume();

        checkDataInit();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //if (onStartCount > 1) {
        if ( onStartCount != 1 ) {
            this.overridePendingTransition(R.animator.in_from_left,
                    R.animator.out_to_right);
        } else {
            onStartCount++;
        }

    }
    private void checkDataInit() {
        if ( Item.items == null || Item.categories == null || Item.pizzasToDisplay == null || Item.order == null) {
            Intent splash = new Intent(this, SplashActivity.class);
            startActivity(splash);
            finish();
        }
    }

}

