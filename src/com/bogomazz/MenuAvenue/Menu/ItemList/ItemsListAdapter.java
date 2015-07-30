package com.bogomazz.MenuAvenue.Menu.ItemList;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.Item.ItemActivity;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 11/1/14.
 */
public class ItemsListAdapter extends ArrayAdapter<Item> {

    private final Item[] items;
    private final Context context;
    private boolean isPizza;
    private String currency;
    private String weight;


    public ItemsListAdapter(Context context, Item[] items, int categoryId) {
        super(context, R.layout.item_list_row, items);
        this.items = items;
        this.context = context;
        this.isPizza = categoryId == Item.CATEGORY_PIZZA;
        this.currency = context.getResources().getString(R.string.currency);

        if (categoryId == Item.CATEGORY_DRINK) {
            this.weight = context.getResources().getString(R.string.capacity);
        } else {
            this.weight = context.getResources().getString(R.string.weight);
        }
    }

    static class ViewHolderRow {
        ImageView itemImage;
        TextView itemName;
        TextView itemCost;
        ToggleButton itemWeight;
        ToggleButton itemWeight2;
        Button plus;
        boolean isFirstWeightPressed = true;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderRow rowViewHolder;
        String weight = items[position].getWeight()+" "+ this.weight;

        if (convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowViewHolder = new ViewHolderRow();

            convertView = inflater.inflate(R.layout.item_list_row, parent, false);
            rowViewHolder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
            rowViewHolder.itemName = (TextView) convertView.findViewById(R.id.itemName);
            rowViewHolder.itemCost = (TextView) convertView.findViewById(R.id.cost);
            rowViewHolder.itemWeight = (ToggleButton) convertView.findViewById(R.id.weight);
            rowViewHolder.plus = (Button) convertView.findViewById(R.id.add);
            if ( isPizza ) {
                rowViewHolder.itemWeight2 = (ToggleButton) convertView.findViewById(R.id.weight2);
                rowViewHolder.itemWeight2.setVisibility(View.VISIBLE);
            }

            convertView.setTag(rowViewHolder);
        } else {
            rowViewHolder = (ViewHolderRow) convertView.getTag();
        }

        DataLoader.loadImageView(rowViewHolder.itemImage, items[position].getImageName());
//        rowViewHolder.itemImage.setBackground(items[position].loadBitmapDrawable());
        rowViewHolder.itemName.setText(items[position].getTitle());
        rowViewHolder.itemCost.setText(items[position].getPrice()+" " + currency);
        rowViewHolder.itemWeight.setTextOn(weight);
        rowViewHolder.itemWeight.setTextOff(weight);
        rowViewHolder.itemWeight.setText(weight);
        rowViewHolder.itemWeight.setChecked(true);

        if (isPizza && position < items.length - Item.NUMBER_OF_CALSONE ) {
            final int secondPizzaIndex = Item.getSecondPizzaFromPositionInList(position);
            weight = Item.items.get(Item.CATEGORY_PIZZA)[secondPizzaIndex].getWeight()+" "+ this.weight;
            rowViewHolder.itemWeight2.setTextOn(weight);
            rowViewHolder.itemWeight2.setTextOff(weight);
            rowViewHolder.itemWeight2.setText(weight);
            rowViewHolder.itemWeight2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Log.d("toggleButton", "work1");
                    if (rowViewHolder.isFirstWeightPressed) {
                        Log.d("toggleButton", "work2");
                        rowViewHolder.itemCost.setText(Item.items.get(Item.CATEGORY_PIZZA)[secondPizzaIndex].getPrice() + " " + currency);
                        rowViewHolder.itemWeight2.setChecked(true);
                        rowViewHolder.itemWeight.setChecked(false);
                        rowViewHolder.isFirstWeightPressed = false;
                    }
                }
            });

        }

        rowViewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (rowViewHolder.itemWeight.isChecked()) {
                    Item.setOrder(items[position], true);
                } else {
                    Item.setOrder(Item.items.get(Item.CATEGORY_PIZZA)[position*2+1], true);
                }

                ItemsListActivity.instance.updateTotal();
            }
        });
        rowViewHolder.itemWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!rowViewHolder.isFirstWeightPressed) {
                    rowViewHolder.itemCost.setText(items[position].getPrice()+" " + currency);
                    rowViewHolder.itemWeight.setChecked(true);
                    rowViewHolder.itemWeight2.setChecked(false);
                    rowViewHolder.isFirstWeightPressed = true;
                }
            }
        });

        return convertView;
    }
}
