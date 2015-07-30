package com.bogomazz.MenuAvenue.Menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.*;
import com.bogomazz.MenuAvenue.Menu.ItemList.ItemsListActivity;
import com.bogomazz.MenuAvenue.Order.OrderActivity;
import com.bogomazz.MenuAvenue.Present.Present;
import android.os.Handler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by andrey on 9/15/14.
 */
public class Menu extends Fragment {
    //private static ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.menu_activity, container, false);

        setUpPagerAdapter(rootView);
        setUpActionBar();
        setUpList(rootView);

        return rootView;
    }

    private void setUpActionBar() {
        TextView title = (TextView) getActivity().findViewById(R.id.title);

        title.setText(getResources().getString(R.string.menu));
    }

    private void setUpList(View rootView) {
        MenuListAdapter adapter = new MenuListAdapter(getActivity(), Item.items.get(Item.CATEGORY));
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ItemsListActivity.class);
                intent.putExtra("categoryId", position);
                Log.d("categoryId in Menu", position+"");
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }


    private void setUpPagerAdapter(View rootView) {
        PagerAdapter adapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        final Handler mainHandler = new Handler();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        final Runnable changeView = new Runnable() {
            private int nextItem = 0;
            public void run(){
                    if (nextItem == PagerAdapter.NUMBER_OF_IMAGE_FRAGMENTS) {
                        nextItem = 0;
                    }
                    viewPager.setCurrentItem(nextItem);
                    nextItem += 1;
                    mainHandler.postDelayed(this, 3000);
            }
        };

//        viewPager.setScrollDurationFactor(2);
        mainHandler.postDelayed(changeView, 3000);
    }


}
