package com.bogomazz.MenuAvenue.Menu.PizzaConstructor;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.R;

import java.util.ArrayList;

/**
 * Created by andrey on 11/23/14.
 */
public class ToppingListAdapter extends ArrayAdapter<Item.Ingredient> {
    private final Context context;
    private ArrayList<Item.Ingredient> ingredients;

    public ToppingListAdapter(Context context, ArrayList<Item.Ingredient> ingredients) {
        super(context, R.layout.pizza_constructor_row, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //rowViewHolder = new ViewHolderRow();
        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.pizza_constructor_row, null);

        final CheckedTextView checkBox = (CheckedTextView) convertView.findViewById(R.id.checkbox);
        if (Item.ingredientsSelected.contains(ingredients.get(position))) {
            checkBox.setChecked(true);
        }
        checkBox.setText(ingredients.get(position).getTitle()+ " " + ingredients.get(position).getPrice() + context.getResources().getString(R.string.currency));
        Log.d(ingredients.get(position).getTitle(), "ingredient");
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if ( !checkBox.isChecked() ) {
                    Item.ingredientsSelected.add(ingredients.get(position));
                    checkBox.setChecked(true);
                    PizzaConstructorActivity.instance.summ += ingredients.get(position).getPrice();
                    PizzaConstructorActivity.instance.updateSumm();
                } else {
                    Item.ingredientsSelected.remove(ingredients.get(position));
                    checkBox.setChecked(false);
                    PizzaConstructorActivity.instance.summ -= ingredients.get(position).getPrice();
                    PizzaConstructorActivity.instance.updateSumm();
                }
            }
        });

        return convertView;
    }
}
