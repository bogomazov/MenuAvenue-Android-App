package com.bogomazz.MenuAvenue.Menu.PizzaConstructor;


import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bogomazz.MenuAvenue.DataLoader;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.ItemList.TabsPagerAdapter;
import com.bogomazz.MenuAvenue.Order.OrderActivity;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.RootActivity;

import java.util.ArrayList;

/**
 * Created by andrey on 11/20/14.
 */
public class PizzaConstructorActivity extends RootActivity {
    public int summ = 0;
    private int averageSizeCost = 43;
    private int largeSizeCost = 53;
    private boolean isAverage = true;
    public static PizzaConstructorActivity instance;

    private final static int AMOUNT_OF_TYPES = 5;

    TextView summTextView;

    private static String toppings[] = {
            "Cheese",
            "Meat",
            "Vegetables",
            "Seafood",
            "Spices"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pizza_constructor);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.pizza_constructor_action_bar);

        Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.selectIngredients), Toast.LENGTH_SHORT);
        toast.show();
        setUpActionBar();
        setUpUI();
        instance = this;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateSumm();

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        instance = null;
    }

    private void setUpActionBar() {
        ((ImageButton) findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        ((ImageButton) findViewById(R.id.checkout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpUI() {
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        final ToggleButton averageSize = (ToggleButton) findViewById(R.id.averageSize);
        final ToggleButton largeSize = (ToggleButton) findViewById(R.id.largeSize);
        summTextView = (TextView) findViewById(R.id.total);

        summ = averageSizeCost;
        isAverage = true;
        updateSumm();
        averageSize.setChecked(true);
        averageSize.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!averageSize.isChecked()) {
                    averageSize.setChecked(true);
                    largeSize.setChecked(false);
                    summ -= largeSizeCost;
                    summ += averageSizeCost;
                    updateSumm();
                    isAverage = true;
                }
                return true;
            }

        });
        largeSize.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!largeSize.isChecked()) {
                    averageSize.setChecked(false);
                    largeSize.setChecked(true);
                    summ += largeSizeCost;
                    summ -= averageSizeCost;
                    updateSumm();
                    isAverage = false;
                }
                return true;
            }

        });

        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.constructor);

        final LinearLayout overallLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        overallLayout.setLayoutParams(layoutParams);
        overallLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams imageButtonParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for ( int i = 0; i < AMOUNT_OF_TYPES; i += 2 ) {
            final LinearLayout lineLayout = new LinearLayout(getApplicationContext());
            lineLayout.setLayoutParams(layoutParams);
            lineLayout.setOrientation(LinearLayout.HORIZONTAL);
            lineLayout.setTag(false);
            for ( int j = 0; j < 2 && (j + i) < AMOUNT_OF_TYPES; j++ ) {
                String imageName = "type_" + Integer.toString(i + j + 1) + ".png";
                final int counter = j;
                final ImageButton toppingImageButton = new ImageButton(this);
                toppingImageButton.setId(i+j);
                toppingImageButton.setBackgroundResource(0);
                toppingImageButton.setLayoutParams(imageButtonParams);
                DataLoader.loadImageView(toppingImageButton, imageName);
//                toppingImageButton.setBackground(Item.loadDrawable(imageName, getApplicationContext()));
                toppingImageButton.setTag(false);
                toppingImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Boolean isListShown = (Boolean) toppingImageButton.getTag();
                        if (!isListShown) {
                            Boolean isListShownUnderLineLayout = (Boolean) lineLayout.getTag();
                            if (isListShownUnderLineLayout) {
                                overallLayout.removeViewAt(overallLayout.indexOfChild(lineLayout) + 1);
                                lineLayout.getChildAt(1 - counter).setTag(false);
                            }
                            ArrayList<Item.Ingredient> toppings = Item.ingredientsType.get(toppingImageButton.getId()+1);
                            ToppingListAdapter adapter = new ToppingListAdapter(getApplicationContext(), toppings);
                            LinearLayout toppingsLayout = new LinearLayout(getApplicationContext());
                            LinearLayout.LayoutParams layoutParams =
                                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            toppingsLayout.setLayoutParams(layoutParams);
                            toppingsLayout.setOrientation(LinearLayout.VERTICAL);
                            for (int i = 0; i < adapter.getCount(); i++) {
                                View item = adapter.getView(i, null, null);
                                toppingsLayout.addView(item);
                            }

                            overallLayout.addView(toppingsLayout, overallLayout.indexOfChild(lineLayout) + 1);

                            toppingImageButton.setTag(true);
                            lineLayout.setTag(true);
//                            Log.d
//                            scrollView.fullScroll(View.FOCUS_DOWN);
                        } else {
                            overallLayout.removeViewAt(overallLayout.indexOfChild(lineLayout) + 1);
                            toppingImageButton.setTag(false);
                            lineLayout.setTag(false);
                        }
                    }
                });
                lineLayout.addView(toppingImageButton);
            }
            overallLayout.addView(lineLayout);
        }
        Log.d("addView", overallLayout.toString());
        linearLayout.addView(overallLayout);

        findViewById(R.id.placeOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Item.ingredientsSelected.isEmpty()) {
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.selectIngredients), Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    String itemName = null;
                    int weight;
                    if (isAverage) {
                        itemName = getResources().getString(R.string.baseSizeSmall);
                        weight = 700;
                    } else {
                        itemName = getResources().getString(R.string.baseSizeBig);
                        weight = 1020;
                    }
                    int []ingredients = new int[Item.ingredientsSelected.size()];
                    int i = 0;
                    for (Item.Ingredient ingredient: Item.ingredientsSelected) {
                        ingredients[i] = ingredient.getId();
                        i++;
                    }
                    Item item = new Item(1, Item.CATEGORY_PIZZA, "Пицца " + itemName, ingredients, weight, summ);
//                    item.initFile(getApplicationContext());
                    item.initFile(getApplicationContext());
                    Item.setOrder(item, true);
                }
            }
        });

    }

    public void updateSumm() {
        summTextView.setText(summ+ " " + getResources().getString(R.string.currency));
    }


}
