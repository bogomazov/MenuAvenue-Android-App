package com.bogomazz.MenuAvenue;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntegerRes;
import android.util.Log;
import android.widget.ImageView;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.util.*;

/**
 * Created by andrey on 9/19/14.
 */
public class Item {

    //Item object's attributes
    private int id;
    private String title;
    private int imageResource;
    private int weight;
    private int price;
    private int categoryId;
    private int[] ingredientsId;
    private File imageFile;

    //statics of Item class

    public final static int NUMBER_OF_CALSONE = 3;

    public static Map<Integer, Item[]> items;
    public static  Map<Integer, String> categories;
    public static Map<Integer, ArrayList<Ingredient>> ingredientsCategory;
    public static Map<Integer, ArrayList<Ingredient>> ingredientsType;
    public static Map<Integer, Ingredient> ingredients;

    public static boolean isLoggedIn = false;

    public static Item[] pizzasToDisplay;

    public static Map<Item, Integer> order;

    public static Set<Ingredient> ingredientsSelected;

    public static int CURRENT_TOTAL = 0;
    public final static int PRESENTS_TO_DISPLAY = 4;

    public static ArrayList<String> tabs;


    public static final int NUMBER_OF_CATEGORIES = 7;

    public static final int CATEGORY_PIZZA = 1;
    public static final int CATEGORY_SUSHI = 2;
    public static final int CATEGORY_TAIBOX = 3;
    public static final int CATEGORY_FREE = 4;
    public static final int CATEGORY_SALAD = 5;
    public static final int CATEGORY_DESERT = 6;
    public static final int CATEGORY_DRINK = 7;
    public static final int CATEGORY_PRESENT = 8;
    public static final int CATEGORY_CONSTRUCTOR = 9;
    public static final int CATEGORY = 9;
    public static boolean isDataLoaded = false;
    public static boolean isIsDataLoadedOneTime = false;




    public static class Present extends Item {
        private String description;

        Present(int id, String presentTitle, String presentDescription) {
            super(id, CATEGORY_PRESENT, presentTitle, null, 0, 0);
            this.description = presentDescription;
        }

        public String getDescription() {
            return description;
        }
    }
    public static class Ingredient {
        private String title;
        private int price;
        private int id;

        public Ingredient(String title, int id, int price) {
            this.id = id;
            this.title = title;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {

            return title;
        }
        public int getPrice() {
            return price;
        }
    }

    public static void init(Database db) {

        if (Locale.getDefault().getLanguage().equals("en")) {
            User.CURRENT_LANGUAGE = User.LANUAGE_ENGLISH;
        } else {
            User.CURRENT_LANGUAGE = User.LANUAGE_RUSSIAN;
        }

        initAllItems(db);
        order = new HashMap<Item, Integer>();

    }

    public static void setOrder(Item item, boolean addOne) {
        if (addOne) {
            if (Item.order.containsKey(item)) {
                int currentQuantity = Item.order.get(item) + 1;
                Item.order.put(item, currentQuantity);

            } else {
                Item.order.put(item, 1);
            }
            Item.CURRENT_TOTAL += item.getPrice();
        } else {
            if (Item.order.containsKey(item)) {
                int currentQuantity = Item.order.get(item);
                currentQuantity -= 1;
                if (currentQuantity == 0) {
                    Item.order.remove(item);
                } else {
                    Item.order.put(item, currentQuantity);
                }
                Item.CURRENT_TOTAL -= item.getPrice();
            }
        }

    }



//    public static void initImageResourcesForAllItems(Context context) {
//        Resources resources = context.getResources();
//        for ( int i = 1; i <= Item.items.size(); i++ ) {
//            for (Item item: Item.items.get(i)) {
////                item.setImageResource(resources.getIdentifier(item.getImageName(), "drawable",
////                        context.getPackageName()));
////                Log.d(item.getImageName() + item.getImageResource(), "imageRes");
//                //Log.d("imageName", item.getImageName());
//                //item.initFile(context);
//            }
//        }
//    }

    public void initFile(Context context) {
        File file = new File(context.getFilesDir(), getImageName());
        if (!file.exists())  {
            file = new File(context.getFilesDir(), "logo.png");
            Log.d(getImageName(), "not exist1!");
        }
        setImageFile(file);
    }

    private static void initAllItems(Database db) {
        items = new HashMap<Integer, Item[]>();
        ingredientsSelected = new HashSet<Ingredient>();
        ingredientsCategory = new HashMap<Integer, ArrayList<Ingredient>>();
        ingredientsType = new HashMap<Integer, ArrayList<Ingredient>>();
        ingredients = new HashMap<Integer, Ingredient>();

        db.loadIngredients();
//        for (int i: ingredientsCategory.keySet()) {
//            for(Ingredient ingredient: ingredientsCategory.get(i)) {
//                Log.d(ingredient.getTitle(), "Ingredient Category");
//            }
//        }
//        for (int i: ingredientsType.keySet()) {
//            for(Ingredient ingredient: ingredientsType.get(i)) {
//                Log.d(ingredient.getTitle(), "Ingredient Type");
//            }
//        }
        boolean inEnglish = true;
        categories = db.getCategories(inEnglish);
        tabs = new ArrayList<String>();
        if (User.CURRENT_LANGUAGE == User.LANUAGE_ENGLISH) {
            for ( int i = 1; i <= Item.CATEGORY_DRINK; i++ ) {
                tabs.add(Item.categories.get(i));
            }
        } else {
            Map<Integer, String> categoriesInRussian = new HashMap<Integer, String>();
            inEnglish = false;

            categoriesInRussian = db.getCategories(inEnglish);
            for ( int i = 1; i <= Item.CATEGORY_DRINK; i++ ) {
                tabs.add(categoriesInRussian.get(i));
            }
        }
        categories.put(CATEGORY, "category");
        for ( int i = 1; i <= CATEGORY_DRINK; i++ ) {
            items.put(i, db.getAllItemsFromCategoryId(i));
        }
        items.put(CATEGORY_PRESENT, db.getAllPresents());
        items.put(CATEGORY_CONSTRUCTOR, new Item[3]);

        items.put(CATEGORY, getAllCategories());
        pizzasToDisplay = getPizzasToDisplay();

        db.close();
    }

    public static int getFirstPizzaFromPositionInList(int position) {
        return position*2;
    }
    public static int getSecondPizzaFromPositionInList(int position) {
        return position*2 +1;
    }

    private static Item[] getPizzasToDisplay() {
        int totalPizzas = Item.items.get(CATEGORY_PIZZA).length;
        int amountToDisplay = (totalPizzas-NUMBER_OF_CALSONE)/2;
        Item pizzas[] = new Item[amountToDisplay+NUMBER_OF_CALSONE];

        for ( int i = 0; i < amountToDisplay; i++ ) {
            pizzas[i] =  Item.items.get(CATEGORY_PIZZA)[i*2];
            Log.d(Integer.toString(pizzas[i].getId()), "pizza to display");
        }

        for ( int j = amountToDisplay, i = totalPizzas-NUMBER_OF_CALSONE; i < totalPizzas; i++, j++ ) {
            pizzas[j] = Item.items.get(CATEGORY_PIZZA)[i];
            Log.d(Integer.toString(pizzas[j].getId()), "pizza to display");
        }
        return pizzas;
    }



    public Item(int id, int categoryId, String title, int[] ingredients, int weight, int price) {
        this.id = id;
        this.title = title;
        this.ingredientsId = ingredients;
        this.weight = weight;
        this.price = price;
        this.categoryId = categoryId;
    }

    public static int getItemPositionInItemsArray(int categoryId, int itemId) {
        int i = 0;
        for (Item item: Item.items.get(categoryId)) {
            if ( item.getId() == itemId ) {
                if ( categoryId == CATEGORY_PIZZA ) {
                    return i - i%2;
                }
                return i;
            }
            i++;
        }
        return -1;
    }


    public int getImageResource() {
        return imageResource;
    }

    public String getTitle(){
        return title;
    }

    public int[] getIngredients() {
        return ingredientsId;
    }

    public int getId() {
        return id;
    }
    public int getCategoryId() {
        return categoryId;
    }

    public int getWeight() {
        return weight;
    }

    public int getPrice() {
        return price;
    }

    public String getImageName() {
        return categories.get(categoryId)+"_"+Integer.toString(id) + ".png";
    }

    public void setImageResource(int resource) {
        this.imageResource = resource;
    }

//    public BitmapDrawable loadBitmapDrawable() {
//        Bitmap bitmap = null;
//        FileInputStream fileInputStream = null;
//        Log.e("Image", imageFile.getName());
//        try {
//            fileInputStream = new FileInputStream(imageFile);
//            bitmap = BitmapFactory.decodeStream(fileInputStream);
//        } catch (FileNotFoundException e) {
//            Log.e("LoadBitmap", e.toString());
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
//        return new BitmapDrawable(bitmap);
//    }

//    public void loadImageView(ImageView imageView) {
//        DataLoader.loadImageView(imageView, getImageName());
//    }
//
//    public static BitmapDrawable loadDrawable(String imageName, Context context) {
//        Bitmap bitmap = null;
//        FileInputStream fileInputStream = null;
//
//        try {
//            File file = new File(context.getFilesDir(), imageName);
//            if (!file.exists()) {
//                file = new File(context.getFilesDir(), "logo.png");
//            }
//            fileInputStream = new FileInputStream(file);
//            bitmap = BitmapFactory.decodeStream(fileInputStream);
//        } catch (FileNotFoundException e) {
//            Log.e("LoadBitmap", e.toString());
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
//        return new BitmapDrawable(bitmap);
//    }

    public  void setImageFile(File file) {
        this.imageFile = file;
    }

    private static Item[] getAllCategories() {
        Item[] categories = new Item[CATEGORY_DRINK];

        for ( int i = 1; i <= CATEGORY_DRINK; i++ ) {
            categories[i-1] = new Item( i, CATEGORY, Item.categories.get(i), null, 0, 0);
        }
        return categories;
    }

//    private static HashMap<Integer, Ingredient[]> initIngredients(Context context) {
//        Resources res = context.getResources();
//        TypedArray toppings =
//                res.obtainTypedArray(R.array.toppings);
//        HashMap<Integer, Ingredient[]> ingredients = new HashMap<Integer, Ingredient[]>();
//        for (int i = 0; i < toppings.length(); i++ ) {
//            String[] stringArray = res.getStringArray(toppings.getResourceId(i, 0));
//            Ingredient ingredientsArray[] = new Ingredient[stringArray.length];
//            int j = 0;
//            for (String title: stringArray) {
//                ingredientsArray[j] = new Ingredient(title, j);
//                j++;
//            }
//            ingredients.put(i, ingredientsArray);
//        }
//        return ingredients;
//    }

}
