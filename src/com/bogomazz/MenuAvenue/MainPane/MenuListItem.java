package com.bogomazz.MenuAvenue.MainPane;

import android.content.res.TypedArray;
import com.bogomazz.MenuAvenue.R;

import java.util.ArrayList;

/**
 * Created by andrey on 11/18/14.
 */
public class MenuListItem {
    public static ArrayList<MenuListItem> items;

    private String title;
    private int icon;

    public static void init(String[] navMenuTitles, TypedArray navMenuIcons) {
        items = new ArrayList<MenuListItem>();

        for (int i = 0; i < navMenuTitles.length ; i++ ) {
            items.add(new MenuListItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }
    }
    public MenuListItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}
