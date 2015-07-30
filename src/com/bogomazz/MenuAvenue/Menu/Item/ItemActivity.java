package com.bogomazz.MenuAvenue.Menu.Item;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Order.OrderActivity;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.RootActivity;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by andrey on 11/1/14.
 */
public class ItemActivity extends RootActivity {
    public static Item item;

    private int indexInArray;
    private int categoryId;
    private TextView quantity;
    private TextView quantity2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.item_action_bar);

        categoryId = getIntent().getExtras().getInt("categoryId");
        if (categoryId == Item.CATEGORY_PIZZA) {
            indexInArray = getIntent().getExtras().getInt("itemIndexInArray");
        }
//        item = (Item) getIntent().getExtras().get("item");
//        Log.d("itemId", indexInArray+"");
//
//        item = Item.items.get(categoryId)[indexInArray];
        setUpActionBar();
        setUpItemUI();

    }

    private void setUpActionBar() {
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        TextView title = (TextView) findViewById(R.id.title);
        Typeface titleFont = Typeface.createFromAsset(getAssets(), "RobotoCondensed-Bold.ttf");
        ImageButton order = (ImageButton) findViewById(R.id.checkout);

//        backButton.();
        title.setTypeface(titleFont);
        title.setText(item.getTitle());

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
    private void setUpItemUI() {
        ImageView imageView = (ImageView) findViewById(R.id.itemImage);
        TextView weight = (TextView) findViewById(R.id.weight);
        quantity = (TextView) findViewById(R.id.quantity);
        Button plus = (Button) findViewById(R.id.plus);
        TextView ingredients = (TextView) findViewById(R.id.ingredients);


        if (item.getCategoryId() == Item.CATEGORY_PIZZA || item.getCategoryId() == Item.CATEGORY_SALAD || item.getCategoryId() == Item.CATEGORY_TAIBOX || item.getCategoryId() == Item.CATEGORY_FREE) {
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(imageView.getLayoutParams().width, imageView.getLayoutParams().height);
            layoutParams.gravity = Gravity.LEFT;
            layoutParams.setMargins(0, -50, 0, 0);
            imageView.setLayoutParams(layoutParams);
//            imageView.setVisibility(View.VISIBLE);
        }
        DataLoader.loadImageView(imageView, item.getImageName());
//        imageView.setBackground(item.loadBitmapDrawable());
        weight.setText(item.getWeight()+" "+getResources().getString(R.string.weight));

        if ( Item.order.containsKey(item) ) {
            updateQuantity();
        } else {
            quantity.setText("0");
        }

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Item.setOrder(item, true);

                updateQuantity();
            }
        });
        plus.setText(R.string.order);

        if (categoryId == Item.CATEGORY_PIZZA) {
            ((RelativeLayout) findViewById(R.id.secondPizza)).setVisibility(View.VISIBLE);
            //int secondWeightPizzaId = Item.getSecondPizzaFromPositionInList(indexInArray+1);
            Log.d("secondWeightPizzaId", indexInArray+1+"");
            final Item item2 = Item.items.get(item.getCategoryId())[indexInArray+1];
            TextView weight2 = (TextView) findViewById(R.id.weight2);
            Button plus2 = (Button) findViewById(R.id.plus2);

            quantity2 = (TextView) findViewById(R.id.quantity2);
            weight2.setText(item2.getWeight()+" "+getResources().getString(R.string.weight));

            if ( Item.order.containsKey(item2) ) {
                updateQuantity2(item2);
            } else {
                quantity2.setText("0");
            }

            plus2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Item.setOrder(item2, true);

                    updateQuantity2(item2);
                }
            });
            plus2.setText(R.string.order);
        }

        ingredients.setText(getIngredients());
    }

    private void updateQuantity() {
        quantity.setText(Item.order.get(item)+"");
    }
    private void updateQuantity2(Item item) {
        quantity2.setText(Item.order.get(item)+"");
    }

    private String getIngredients() {
        StringBuilder ingredientsStr = new StringBuilder();


        for (int i: item.getIngredients()) {
            Log.d(i+"", "Id");
            String ingredientName = Item.ingredients.get(i).getTitle();
            ingredientsStr.append(Character.toLowerCase(ingredientName.charAt(0))+ingredientName.substring(1)+", ");
        }
        if (ingredientsStr.length() > 2 ) {
            ingredientsStr.replace(0, 1, Character.toUpperCase(ingredientsStr.toString().charAt(0)) + "");
            ingredientsStr.replace(ingredientsStr.length() - 2, ingredientsStr.length()-1, ".");
        }

        return ingredientsStr.toString();
    }
}
