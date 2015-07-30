package com.bogomazz.MenuAvenue.Menu.ItemList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.bogomazz.MenuAvenue.Item;

/**
 * Created by andrey on 10/30/14.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Log.d("index", index+"");
        Fragment listFragment = new ItemsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("index", index);
        listFragment.setArguments(bundle);

        return listFragment;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return Item.NUMBER_OF_CATEGORIES;
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

}
