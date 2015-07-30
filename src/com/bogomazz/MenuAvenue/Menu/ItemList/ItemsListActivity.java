package com.bogomazz.MenuAvenue.Menu.ItemList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.MainPane.MainActivity;
import com.bogomazz.MenuAvenue.Order.OrderActivity;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.RootActivity;

import java.util.ArrayList;

/**
 * Created by andrey on 10/30/14.
 */
public class ItemsListActivity extends RootActivity implements
        ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private TextView total;
    public static ItemsListActivity instance;

    // Tab titles
    private ArrayList<String> tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_list);

        setUpViewPager();
        setUpActionBar();

        viewPager.setCurrentItem(getIntent().getIntExtra("categoryId", -1), false);
        instance = this;

    }
    @Override
    public void onResume(){
        super.onResume();
        updateTotal();

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        instance = null;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    private void setUpActionBar() {
        actionBar = getActionBar();
        tabs = Item.tabs;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.item_list_action_bar);

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        //Bug FIX TABS APPEARED ABOVE ACTION BAR
        actionBar.setDisplayShowHomeEnabled(true);
        View homeIcon = findViewById(android.R.id.home);
        ((View) homeIcon.getParent()).setVisibility(View.GONE);

        total = (TextView) findViewById(R.id.total);
        total.setText(Item.CURRENT_TOTAL+"");

        //init tabs



        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        ((ImageButton) findViewById(R.id.checkout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

    }
    private void setUpViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
                //mAdapter.notifyDataSetChanged();
                //ListFragment fragment = (ListFragment) mAdapter.instantiateItem(viewPager, position);

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void updateTotal() {
        TextView totalView = (TextView) findViewById(R.id.total);

        totalView.setText(Item.CURRENT_TOTAL+" "+getApplicationContext().getResources().getString(R.string.currency));

    }

}
