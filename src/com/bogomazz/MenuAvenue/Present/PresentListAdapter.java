package com.bogomazz.MenuAvenue.Present;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 9/18/14.
 */
public class PresentListAdapter extends ArrayAdapter<Item> {
    Item[] items;
    Context context;

    static class ViewHolderRow {
        ImageView image;
        TextView title;
    }

    public PresentListAdapter(Context context, Item[] items) {
        super(context, R.layout.present_row, items);
        Log.e("Number", Integer.toString(items.length));
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderRow rowViewHolder;

        if (convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.present_row, parent, false);
            rowViewHolder = new ViewHolderRow();

            rowViewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            rowViewHolder.title = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(rowViewHolder);

        } else {
            rowViewHolder = (ViewHolderRow) convertView.getTag();
        }
        Item item = items[position];

        if (item != null ) {
            DataLoader.loadImageView(rowViewHolder.image, item.getImageName());
//            rowViewHolder.image.setBackground(item.loadBitmapDrawable());
            rowViewHolder.title.setText(item.getTitle());
        }


        return convertView;
    }
}
