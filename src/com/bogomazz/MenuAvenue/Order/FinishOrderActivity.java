package com.bogomazz.MenuAvenue.Order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.MainPane.MainActivity;
import com.bogomazz.MenuAvenue.Order.Checkout.CheckoutActivity;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 1/4/15.
 */
public class FinishOrderActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_order);


        findViewById(R.id.moveToMainPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
