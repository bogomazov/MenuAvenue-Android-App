package com.bogomazz.MenuAvenue.MainPane;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bogomazz.MenuAvenue.R;

import java.util.ArrayList;


/**
 * Created by andrey on 11/18/14.
 */
public class MenuListAdapter extends ArrayAdapter<MenuListItem> {
    private final Context context;
    private ArrayList<MenuListItem> navDrawerItems;

    public MenuListAdapter(Context context, ArrayList<MenuListItem> navDrawerItems) {
        super(context, R.layout.menu_row, navDrawerItems);
        this.navDrawerItems = navDrawerItems;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        //rowViewHolder = new ViewHolderRow();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.main_menu_list_item, null);
        }
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        TextView textView = (TextView) convertView.findViewById(R.id.title);


//        imageView.setImageResource(navDrawerItems.get(position).getIcon());
        textView.setText(navDrawerItems.get(position).getTitle());


        return convertView;
    }

}