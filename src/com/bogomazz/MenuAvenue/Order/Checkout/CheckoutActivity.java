package com.bogomazz.MenuAvenue.Order.Checkout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.bogomazz.MenuAvenue.*;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import com.bogomazz.MenuAvenue.Order.OrderActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by andrey on 11/5/14.
 */
public class CheckoutActivity extends RootActivity {
    public Date orderDate;

    private DeliveryFragment deliveryFragment;
    private PickUpFragment pickUpFragment;
    private boolean isDeliveryFragmentShown = true;
    private boolean apply = false;
    private ToggleButton deliveryButton;
    private ToggleButton pickupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.checkout_action_bar);

        setUpActionBar();
        setUpFragments();
        changeFragment(isDeliveryFragmentShown);
    }
    @Override
    public void onResume() {
        super.onResume();
//        if ( isDeliveryFragmentShown ) {
//            deliveryButton.setPressed(true);
//            pickupButton.setPressed(false);
//        } else {
//            deliveryButton.setPressed(false);
//            pickupButton.setPressed(true);
//        }
    }

    public void postOrder(String addressId, String phoneNumber, String comment, boolean isDeliverInOneHour) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("user_id", User.userId+"");
            Log.e("userID", User.userId+"");
            jsonObject.put("address_id", addressId);
            jsonObject.put("phone", phoneNumber);
            jsonObject.put("comment", DataLoader.convertToUTF8(comment));
            jsonObject.put("payment_sys", DataLoader.convertToUTF8("Наличными"));
            jsonObject.put("summ", Item.CURRENT_TOTAL + "");

            if (isDeliverInOneHour) {
                jsonObject.put("delivery_time", DataLoader.convertToUTF8("Доставить за час"));
            } else {
                jsonObject.put("delivery_time", getDate());
            }
        } catch (JSONException e) {
            Log.e(e.toString(), "json exception");
        }

        addOrdersToJSON(jsonObject);

        try {
            String response = new PostOrder().execute(jsonObject).get();
            Log.d(response, "response");
        } catch (Exception e) {
            Log.e(e.toString(), "postOrder func exception");
        }
    }

    private void addOrdersToJSON(JSONObject jsonObject) {
        int i = 0;
        JSONArray orders = new JSONArray();
        try {
            for (Item item : Item.order.keySet()) {
                JSONArray itemArray = new JSONArray();

                itemArray.put(DataLoader.convertToUTF8(item.getTitle()));
                itemArray.put(DataLoader.convertToUTF8(item.getWeight() + " г"));
                itemArray.put(DataLoader.convertToUTF8(item.getPrice() + " грн"));
                itemArray.put(DataLoader.convertToUTF8(Item.order.get(item) + " ед."));
                orders.put(itemArray);
                i++;
            }

            jsonObject.put("order", orders);
        } catch (JSONException e) {
            Log.e(e.toString(), "json exception");
        }
    }

    private void setUpActionBar() {
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        TextView title = (TextView) findViewById(R.id.title);
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");

        title.setTypeface(titleFont);
        title.setText(getResources().getString(R.string.checkout));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

    }
    private void setUpFragments() {
        deliveryButton = (ToggleButton)findViewById(R.id.deliveryButton);
        pickupButton = (ToggleButton) findViewById(R.id.pickupButton);

        deliveryFragment = new DeliveryFragment();
        pickUpFragment = new PickUpFragment();
        pickupButton.setText(getResources().getString(R.string.pickup));
        pickupButton.setTextOff(getResources().getString(R.string.pickup));
        pickupButton.setTextOn(getResources().getString(R.string.pickup));
        deliveryButton.setText(getResources().getString(R.string.delivery));
        deliveryButton.setTextOn(getResources().getString(R.string.delivery));
        deliveryButton.setTextOff(getResources().getString(R.string.delivery));
        deliveryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isDeliveryFragmentShown) {
                    changeFragment(!isDeliveryFragmentShown);
                    deliveryButton.setChecked(true);
                    pickupButton.setChecked(false);

                }
                return true;
            }

        });
        pickupButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDeliveryFragmentShown) {
                    changeFragment(!isDeliveryFragmentShown);
                    pickupButton.setChecked(true);
                    deliveryButton.setChecked(false);
                }
                return true;
            }
        });

        deliveryButton.setChecked(true);

        }

    private void changeFragment(boolean toShowDeliveryFragment) {
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        if (toShowDeliveryFragment) {
            fragTransaction.replace(R.id.fragmentContainer, deliveryFragment);
            isDeliveryFragmentShown = true;
        } else {
            fragTransaction.replace(R.id.fragmentContainer, pickUpFragment);
            isDeliveryFragmentShown = false;
        }
        //fragTransaction.replace(R.id.fragmentContainer, pickUpFragment);
        fragTransaction.commit();
    }

    public void showCustomTimeDialog() {
//        Calendar mcurrentDate = Calendar.getInstance();
//        int mMonth = mcurrentDate.get(Calendar.MONTH);
//        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
//        int mHour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
//        int mMinute = mcurrentDate.get(Calendar.MINUTE);
//
//        DatePickerDialog mDatePicker;
//        mDatePicker = new DatePickerDialog(DeliveryFragment.this, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
//                // TODO Auto-generated method stub
//                    /*      Your code   to get date and time    */
//                selectedmonth = selectedmonth + 1;
//                eReminderDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
//            }
//        }, mMonth, mDay);
//        mDatePicker.setTitle("Select time and date");
//        mDatePicker.show();
//        AlertDialog alertDialog = new AlertDialog.Builder(instance).create();
//        alertDialog.setTitle(getResources().getString(R.string.thanksForUnderst));
//        alertDialog.setMessage(getResources().getString(R.string.beCarefull));
//        alertDialog.setIcon(R.drawable.ic_logo);
//        alertDialog.show();
        final Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.checkout_custom_time, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timePicker);
                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);

                        setDate(
                                datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute()
                        );



                        Toast toast = Toast.makeText(getApplicationContext(), getDate(), Toast.LENGTH_LONG);
                        toast.show();

                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
        });
        dialog = builder.create();
        dialog.show();

        return ;
    }

    public void submit() {
        if (isDeliveryFragmentShown) {

        }
    }



    public void setDate( int year, int month, int day, int hour, int minute ) {
        String dateInString = year + "-" + month + "-" + day + " " + hour + ":" + minute;
//        String oldstring = "2011-01-18 00:00:00.0";
        try {
            orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateInString);
        } catch (ParseException e) {
            Log.d("ParseException", e.toString());
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    public String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(orderDate);
    }

}
