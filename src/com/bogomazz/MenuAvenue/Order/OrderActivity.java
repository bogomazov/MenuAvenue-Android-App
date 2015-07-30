package com.bogomazz.MenuAvenue.Order;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.Item.ItemActivity;
import com.bogomazz.MenuAvenue.Menu.ItemList.ItemsListAdapter;
import com.bogomazz.MenuAvenue.Order.Checkout.CheckoutActivity;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.RootActivity;

/**
 * Created by andrey on 11/2/14.
 */
public class OrderActivity extends RootActivity {
    public static OrderActivity instance;
    private Item orderItems[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.checkout_action_bar);

        setUpActionBar();
        setUpList();
        setUpBottom();
        instance = this;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        setUpList();
    }

    private void setUpList() {
        ListView itemsListView = (ListView) findViewById(R.id.listView);
        orderItems = Item.order.keySet().toArray(new Item[Item.order.keySet().size()]);
        OrderListAdapter adapter = new OrderListAdapter(getApplicationContext(), orderItems);

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);
//                OrderListAdapter.ViewHolderRow rowViewHolder = (OrderListAdapter.ViewHolderRow) view.getTag();
                if (orderItems[position].getCategoryId() == Item.CATEGORY_PIZZA) {
                    intent.putExtra("categoryId", Item.CATEGORY_DRINK);
                } else {
                    intent.putExtra("categoryId", orderItems[position].getCategoryId());
                }
                ItemActivity.item = orderItems[position];
                Log.d("Click", "some");
                startActivity(intent);
            }
        });
        itemsListView.setAdapter(adapter);
    }
    private void setUpActionBar() {
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        TextView title = (TextView) findViewById(R.id.title);

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        title.setTypeface(titleFont);
        title.setText(getResources().getText(R.string.busket));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
    public void updateTotal() {
        TextView totalView = (TextView) findViewById(R.id.total);

        totalView.setText(Item.CURRENT_TOTAL+" "+getApplicationContext().getResources().getString(R.string.currency));
    }
    private void setUpBottom() {
        Button placeOrder = (Button) findViewById(R.id.placeOrder);

        placeOrder.setText(getResources().getText(R.string.placeOrder));
        updateTotal();
        ((TextView) findViewById(R.id.sumLabel)).setText(getResources().getText(R.string.sum));

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( Item.CURRENT_TOTAL >= 100 ) {
                    Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(instance).create();
                    alertDialog.setTitle(getResources().getString(R.string.thanksForUnderst));
                    alertDialog.setMessage(getResources().getString(R.string.beCarefull));
                    alertDialog.setIcon(R.drawable.ic_logo);
                    alertDialog.show();
                }
            }
        });

    }
}
