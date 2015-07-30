package com.bogomazz.MenuAvenue.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.ItemList.ItemsListActivity;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 11/3/14.
 */
public class OrderListAdapter extends ArrayAdapter<Item> {

    private final Item[] items;
    private final Context context;
    private String currency;
    private String weight;

    public OrderListAdapter(Context context, Item[] items) {
        super(context, R.layout.order_list_row, items);
        this.items = items;
        this.context = context;
        this.currency = context.getResources().getString(R.string.currency);
        this.weight = context.getResources().getString(R.string.weight);
    }

    public static class ViewHolderRow {
        ImageView itemImage;
        TextView itemName;
        ToggleButton itemCost;
        TextView quantity;
        Button itemWeight;
        Button ItemWeight2;
        Button plus;
        Button minus;
        int itemId;
        int categoryId;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolderRow rowViewHolder;

        if (convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowViewHolder = new ViewHolderRow();

            convertView = inflater.inflate(R.layout.order_list_row, parent, false);
            rowViewHolder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
            rowViewHolder.itemName = (TextView) convertView.findViewById(R.id.itemName);
            rowViewHolder.itemCost = (ToggleButton) convertView.findViewById(R.id.cost);
            rowViewHolder.itemWeight = (Button) convertView.findViewById(R.id.weight);
            rowViewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
            //rowViewHolder.itemWeight = (Button) convertView.findViewById(R.id.weight2);
            rowViewHolder.plus = (Button) convertView.findViewById(R.id.plus);
            rowViewHolder.minus = (Button) convertView.findViewById(R.id.minus);

            convertView.setTag(rowViewHolder);
        } else {
            rowViewHolder = (ViewHolderRow) convertView.getTag();
        }

        DataLoader.loadImageView(rowViewHolder.itemImage, items[position].getImageName());
//        rowViewHolder.itemImage.setBackground(items[position].loadBitmapDrawable());
        rowViewHolder.itemName.setText(items[position].getTitle());
        rowViewHolder.itemCost.setText(items[position].getPrice()+" " + currency);
        rowViewHolder.itemWeight.setText(items[position].getWeight()+" "+ weight);
        if(Item.order.containsKey(items[position])) {
            rowViewHolder.quantity.setText(Item.order.get(items[position]) + "");
        } else {
            rowViewHolder.quantity.setText("0");
        }

        rowViewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Item.setOrder(items[position], true);
                rowViewHolder.quantity.setText(Item.order.get(items[position])+"");
                OrderActivity.instance.updateTotal();
            }
        });
        rowViewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Item.setOrder(items[position], false);
                if (Item.order.get(items[position]) == null) {
                    rowViewHolder.quantity.setText("0");
                } else {
                    rowViewHolder.quantity.setText(Item.order.get(items[position]) + "");
                }
                OrderActivity.instance.updateTotal();
            }
        });
        rowViewHolder.itemId = items[position].getId();
        rowViewHolder.categoryId = items[position].getCategoryId();
        return convertView;
    }
}

