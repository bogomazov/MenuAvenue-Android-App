package com.bogomazz.MenuAvenue.Menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 9/18/14.
 */
public class MenuListAdapter extends ArrayAdapter<Item> {
    private final Item[] items;
    private final Context context;
    private int nextRow;
//    private final Activity context;
//    private ImageLoader imageLoader;

    public MenuListAdapter(Context context, Item[] items) {
        super(context, R.layout.menu_row, items);
        this.items = items;
        this.context = context;

    }

    static class ViewHolderRow {
        ImageView imageView;
        TextView textView;
    }
    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (getItemViewType(position) == 0) {
            convertView = inflater.inflate(R.layout.menu_row, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.menu_row_reverse, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        TextView textView = (TextView) convertView.findViewById(R.id.title);

        DataLoader.loadImageView(imageView, items[position].getImageName());
//        imageView.setBackground(items[position].loadBitmapDrawable());
//        DataLoader.loadImageView(imageView, items[position].getImageName());
        textView.setText(Item.tabs.get(position).toUpperCase());
        textView.setTextSize(20.0f);


        return convertView;
    }

}
