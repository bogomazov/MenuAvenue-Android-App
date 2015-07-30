package com.bogomazz.MenuAvenue.MainPane;


import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.Menu;
import com.bogomazz.MenuAvenue.Menu.PizzaConstructor.PizzaConstructorActivity;
import com.bogomazz.MenuAvenue.Order.OrderActivity;
import com.bogomazz.MenuAvenue.Present.Present;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.RootActivity;

import java.util.ArrayList;

/**
 * Created by andrey on 11/18/14.
 */
public class MainActivity extends RootActivity {
    final public static String MENU_OPTION = "menuOption";
    final public static int DRAWER_OPTION_MENU = 0;
    final public static int DRAWER_OPTION_PRESENT = 1;
    private DrawerLayout mDrawerLayout;
    public static int currentMenuOption = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_pane);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        setUpActionBar();

        //init drawer items

        Bundle bundle = getIntent().getExtras();
        //first time init
        if ( bundle == null ) {
            displayView(0);
        } else {
            displayView(bundle.getInt(MENU_OPTION));
        }
        setUpDrawer();
    }
    public void displayView(int position) {
        // update the main content by replacing fragments

        currentMenuOption = position;
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new Menu();
                break;
            case 1:
                fragment = new Present();
                break;
            case 2:
                Intent intent = new Intent(getApplicationContext(), PizzaConstructorActivity.class);
                startActivity(intent);
                return;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    private void setUpActionBar() {
        TextView title = (TextView)  findViewById(R.id.title);
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        ImageButton icon = (ImageButton) findViewById(R.id.ic_logo);
        ImageButton order = (ImageButton) findViewById(R.id.checkout);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                Log.d("DrawerLatout", "check");
            }
        });
        title.setTypeface(titleFont);
    }

    private void setUpDrawer() {
        //init menu items
        MenuListItem.init(getResources().getStringArray(R.array.nav_drawer_items), getResources()
                .obtainTypedArray(R.array.nav_drawer_icons));

        MenuListAdapter adapter = new MenuListAdapter(this, MenuListItem.items);
        LinearLayout menuListLayout = (LinearLayout) findViewById(R.id.menuList);

        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, null);
            item.setTag(i);
            item.setOnClickListener(new SlideMenuClickListener());
            menuListLayout.addView(item);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ((ImageView) findViewById(R.id.logo)).setImageResource(R.drawable.logo_menuavenue);
//        ((ImageView) findViewById(R.id.logo)).setImageResource(Item.loadDrawable("logo.png", getApplicationContext()));
        ((TextView) findViewById(R.id.callNumber)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:" + getResources().getString(R.string.phoneNumberWork)));
                startActivity(dial);
            }
        });
        ((TextView) findViewById(R.id.menuavenueLink)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.MenuAvenueLink)));
                startActivity(browserIntent);
            }
        });
        ((TextView) findViewById(R.id.bogomazzLink)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.BogomazzLink)));
                startActivity(browserIntent);
            }
        });
        findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/MenuAvenue"));
                startActivity(browserIntent);
            }
        });
    }

    private class SlideMenuClickListener implements
            View.OnClickListener {
        @Override
        public void onClick(View view) {
            // display view for selected nav drawer item
            displayView(((Integer)view.getTag()));
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */


}
