package com.bogomazz.MenuAvenue.Menu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter ;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.MainPane.MainActivity;
import com.bogomazz.MenuAvenue.Present.Present;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 10/24/14.
 */
public class PagerAdapter extends FragmentStatePagerAdapter  {
    public static final int NUMBER_OF_IMAGE_FRAGMENTS = 5;
    private static int currentItem = 0;

    public static class FragmentImageView extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.image, container, false);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ((MainActivity) getActivity()).displayView(MainActivity.DRAWER_OPTION_PRESENT);

                }
            });
            Log.d(currentItem+"", "currentItem");
            DataLoader.loadImageView((ImageView) view.findViewById(R.id.imageView), Item.items.get(Item.CATEGORY_PRESENT)[currentItem].getImageName());
//                    ((ImageView) view.findViewById(R.id.imageView)).setBackground(Item.items.get(Item.CATEGORY_PRESENT)[currentItem].loadBitmapDrawable());
            return view;

        }
    }
    PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int itemId) {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        currentItem = itemId;
        FragmentImageView fragmentImageView = new FragmentImageView();


        return fragmentImageView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return NUMBER_OF_IMAGE_FRAGMENTS;
    }

}
