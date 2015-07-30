package com.bogomazz.MenuAvenue.Order.Checkout;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Order.FinishOrderActivity;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.User;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by andrey on 11/5/14.
 */
public class PickUpFragment extends Fragment {
    private static String[] streets;
    private boolean isDeliveryIn15Min = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.checkout_pickup_fragment, container, false);

        setUpUI(rootView);

        return rootView;
    }

    private void setUpUI(View rootView) {
        TextView addressTitle = (TextView) rootView.findViewById(R.id.deliveryAddress);
        TextView subsidaryName = (TextView) rootView.findViewById(R.id.pickUpPlace);
        final AutoCompleteTextView subsidary = (AutoCompleteTextView) rootView.findViewById(R.id.pickUp);

        TextView informationTitle = (TextView) rootView.findViewById(R.id.information);
        TextView emailName = (TextView) rootView.findViewById(R.id.emailName);
        final EditText emailInput = (EditText) rootView.findViewById(R.id.email);
        TextView phoneName = (TextView) rootView.findViewById(R.id.phoneNumberName);
        final EditText phoneInput = (EditText) rootView.findViewById(R.id.phoneNumber);

        final ToggleButton deliverIn15minutes = (ToggleButton) rootView.findViewById(R.id.in15min);
        final ToggleButton setOtherTime = (ToggleButton) rootView.findViewById(R.id.otherTime);

        TextView commentTitle = (TextView) rootView.findViewById(R.id.commentTitle);
        final EditText commentInput = (EditText) rootView.findViewById(R.id.comment);

        Button submitOrder = (Button) rootView.findViewById(R.id.submitOrder);

        if (User.email != null) {
            rootView.findViewById(R.id.emailId).setVisibility(View.GONE);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, streets);
        subsidary.setAdapter(adapter);
        subsidary.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                subsidary.showDropDown();
                subsidary.requestFocus();
                return false;
            }
        });

        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                if (!DataLoader.isNetworkAvailable(getActivity())) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.internet_exception), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent intent = new Intent(getActivity().getApplicationContext(), FinishOrderActivity.class);
                    startActivity(intent);

                    if (User.email == null && User.userId == null) {
                        User.initUser(emailInput.toString(), phoneInput.toString());
                    }
                    String phoneNumber = phoneInput.getText().toString();
                    //90, 91
                    if (subsidary.getText().toString().equals(streets[0])) {
                        params.add(new BasicNameValuePair("table", "address"));
                        params.add(new BasicNameValuePair("user_id", User.userId));
                        String encodedAddressStreet = DataLoader.convertToUTF8("ул.Милославская");
                        params.add(new BasicNameValuePair("address_street", encodedAddressStreet));
                        Log.e("address_street", encodedAddressStreet);
                        params.add(new BasicNameValuePair("address_home", "43-A"));
                        params.add(new BasicNameValuePair("address_flat", "?"));
                        params.add(new BasicNameValuePair("address_floor", "2"));
                    } else if (subsidary.getText().toString().equals(streets[1])) {
                        params.add(new BasicNameValuePair("table", "address"));
                        params.add(new BasicNameValuePair("user_id", User.userId));
                        String encodedAddressStreet = DataLoader.convertToUTF8("ул.Демеевская");
                        params.add(new BasicNameValuePair("address_street", encodedAddressStreet));
                        Log.e("address_street", encodedAddressStreet);
                        params.add(new BasicNameValuePair("address_home", "43-A"));
                        params.add(new BasicNameValuePair("address_flat", "?"));
                        params.add(new BasicNameValuePair("address_floor", "1"));
                    } else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильныя улица", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }


                    String addressId = null;

                    try {
                        addressId = new DeliveryFragment.PostAddress().execute(params).get();
                        String comment = DataLoader.convertToUTF8(commentInput.getText().toString());
                        Log.e(addressId + "", "AddressId");
                    } catch (InterruptedException e) {
                        Log.e("Interrupted exception", e.toString());
                    } catch (ExecutionException e) {
                        Log.e("Execution exception", e.toString());
                    }
                    String comment = DataLoader.convertToUTF8(commentInput.getText().toString());
                    ((CheckoutActivity) getActivity()).postOrder(addressId, phoneNumber, comment, isDeliveryIn15Min);
                    getActivity().finish();
                }
            }
        });

        subsidaryName.setText(R.string.ourSubsidary);
        addressTitle.setText(R.string.information);
        emailName.setText(R.string.email);
        phoneName.setText(R.string.phone);
        deliverIn15minutes.setText(R.string.in15minutes);
        setOtherTime.setText(R.string.otherTime);
        commentTitle.setText(R.string.comment);
        submitOrder.setText(R.string.submitOrder);

        deliverIn15minutes.setChecked(true);
        deliverIn15minutes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    deliverIn15minutes.setChecked(true);
                    return true;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    deliverIn15minutes.setChecked(false);
                    return false;
                }

                if (!isDeliveryIn15Min) {

                    deliverIn15minutes.setChecked(true);
                    setOtherTime.setChecked(false);
                    isDeliveryIn15Min = true;
                }
                return true;
            }

        });
        setOtherTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                    setOtherTime.setChecked(true);
                    return true;
                }
                if (event.getAction()!=MotionEvent.ACTION_UP) {
                    setOtherTime.setChecked(false);
                    return false;
                }
                ((CheckoutActivity) getActivity()).showCustomTimeDialog();
                if (isDeliveryIn15Min) {
                    deliverIn15minutes.setChecked(false);
                    setOtherTime.setChecked(true);
                    isDeliveryIn15Min = false;
                }
                return true;
            }

        });
    }



    static {
        streets = new String[] {
                "ул.Милославская 43-A, 2 этаж.",
                "ул.Демеевская 43-А, 1 этаж"
        };
    }
}
