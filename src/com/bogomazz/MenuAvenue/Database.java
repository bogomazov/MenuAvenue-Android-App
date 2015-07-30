package com.bogomazz.MenuAvenue;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.bogomazz.MenuAvenue.Exception.NoNetworkException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrey on 10/4/14.
 */
public class Database extends SQLiteOpenHelper {
    // All Static variables
    public static double VERSION = 0.9;
    public static final String DATABASE_FILE_NAME = "menuavenue.db";
    private static final int DATABASE_VERSION = 3;

    public static File DATABASE_FILE;
    public static String DATABASE_PATH;

    // Items table name
    private static final String TABLE_ITEMS = "item";

    // Ingredient table name
    private static final String TABLE_INGREDIENT = "ingredient";

    // Ingredient Table Columns names
    private static final String INGREDIENT_ID = "ingredient_id";

    // // ITEM_INGREDOINT table name
    private static final String ITEM_INGREDIENT = "item_ingredient";


    // Price table name
    private static final String TABLE_PRICE = "price";



    public Database(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    public static void load(Context context) throws NoNetworkException{
        DATABASE_FILE = new File("/data/data/" + context.getPackageName() + "/databases/" + DATABASE_FILE_NAME);
        DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        DatabaseLoader databaseLoader = new DatabaseLoader(context);

        databaseLoader.run();
        Log.d("Called load2", "called");
        if (!Database.DATABASE_FILE.exists()) {
            Log.d("Copy from Assets", "works!");
            copyFromAssetsToStorage(context);
        }
    }

    private static void copyFromAssetsToStorage(Context context) {
        InputStream mInput = null;
        OutputStream mOutput = null;
        String outFileName = Database.DATABASE_FILE.getAbsolutePath();
        File fileDir = new File(Database.DATABASE_PATH);

        //create folders if not created
        if(!fileDir.exists()) {
            fileDir.mkdirs();
        }

        try {
            try {
                mInput = context.getAssets().open(Database.DATABASE_FILE_NAME);
            } catch (IOException e) {
                Log.e("Assets file", "file");

            }
            try {
                Log.d("Path", outFileName);
                mOutput = new FileOutputStream(outFileName);
            } catch (IOException e) {
                Log.d("Output", "file");
            }
            byte[] mBuffer = new byte[1024];
            int mLength;
            if (mInput == null) {
                Log.e("Fail", "LOL");
            }
            for (;(mLength = mInput.read(mBuffer))>0;)
            {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        } catch (IOException e) {
            Log.d(e.toString(), "InputStream or output stream");
        }
    }


    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_INGREDIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRICE);

        // Create tables again
        onCreate(db);
    }

    public Item[] getAllItemsFromCategoryId(int categoryId) {
        String query = "SELECT  * FROM " + TABLE_ITEMS + " WHERE category_id=" + categoryId;
        List<Item> items = new ArrayList<Item>();
        Cursor cursor = getCursorForQuery(query);

        if (cursor.moveToFirst()) {
            do {
                int itemId = cursor.getInt(0);
                String title = null;
                if ( User.CURRENT_LANGUAGE == User.LANUAGE_ENGLISH ) {
                    title = cursor.getString(5);
                } else {
                    title = cursor.getString(2);
                }
                int weight = cursor.getInt(3);
                int price = cursor.getInt(4);
                items.add(new Item(itemId, categoryId, title, getIngredientsFromItemId(itemId), weight, price));
            } while (cursor.moveToNext());
        }
        return items.toArray(new Item[items.size()]);
    }

    public Item.Present[] getAllPresents() {
        String query = "SELECT  * FROM present";
        List<Item.Present> presents = new ArrayList<Item.Present>();
        Cursor cursor = getCursorForQuery(query);

        if (cursor.moveToFirst()) {
            do {
                int presentId = cursor.getInt(0);
                String title = null;
                String description = null;
                if ( User.CURRENT_LANGUAGE == User.LANUAGE_ENGLISH ) {
                    title = cursor.getString(2);
//                    description = cursor.getString(4);
                } else {
                    title = cursor.getString(1);
//                    description = cursor.getString(3);
                }
                presents.add(new Item.Present(presentId, title, description));
            } while (cursor.moveToNext());
        }
        return presents.toArray(new Item.Present[presents.size()]);
    }

    private int[] getIngredientsFromItemId(int itemId) {
        String query = "SELECT " + INGREDIENT_ID + " FROM " + ITEM_INGREDIENT + " natural join ingredient WHERE item_id=" + itemId;
        List<Integer> ingredients = new ArrayList<Integer>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        // return contact list
        return fromListIntegerToIntArray(ingredients);
    }

    public HashMap<Integer, String> getAllIngredients() {
        String query = "SELECT * FROM ingredient";
        HashMap<Integer, String> ingredients = new HashMap<Integer, String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ingredients.put(cursor.getInt(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // return contact list
        return ingredients;
    }

    public void loadIngredients() {
        String query = "SELECT * FROM ingredient order by ingredient_type_id";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

//        Map<Integer, ArrayList<Item.Ingredient>>[] arrayListMap;
//        Map<Integer, ArrayList<Item.Ingredient>> ingredientsCategory = new HashMap<Integer, ArrayList<Item.Ingredient>>(),
//        Map<Integer, ArrayList<Item.Ingredient>> ingredientsType = new HashMap<Integer, ArrayList<Item.Ingredient>>(),
//        ingredientsCategory =

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = null;
                if ( User.CURRENT_LANGUAGE == User.LANUAGE_ENGLISH ) {
                    title = cursor.getString(2);
                } else {
                    title = cursor.getString(1);
                }
                int price = cursor.getInt(3);
                int categoryId = cursor.getInt(4);
                int ingredientTypeId = cursor.getInt(5);

                if (!Item.ingredientsCategory.containsKey(categoryId)) {
                    Item.ingredientsCategory.put(categoryId, new ArrayList<Item.Ingredient>());
                }
                if (!Item.ingredientsType.containsKey(ingredientTypeId)) {
                    Item.ingredientsType.put(ingredientTypeId, new ArrayList<Item.Ingredient>());
                }

                Item.Ingredient ingredient = new Item.Ingredient(title, id, price);
                Item.ingredientsCategory.get(categoryId).add(ingredient);
                Item.ingredientsType.get(ingredientTypeId).add(ingredient);
                Item.ingredients.put(id, ingredient);
            } while (cursor.moveToNext());
        }
    }




    private int[] fromListIntegerToIntArray(List<Integer> list) {
        Integer []integerArray = list.toArray(new Integer[list.size()]);
        int []intArray = new int[integerArray.length];

        for (int i = 0; i < integerArray.length; i++ ) {
            intArray[i] = integerArray[i];
        }
        return intArray;
    }


    public HashMap<Integer, String> getCategories(boolean inEnglish) {
        HashMap<Integer, String> categories = new HashMap<Integer, String>();
        String query = "SELECT * FROM category";
        Cursor cursor = getCursorForQuery(query);
        int titleRow;
        if (inEnglish) {
            titleRow = 1;
        } else {
            titleRow = 2;
        }
//        titleRow = 1;

        if (cursor.moveToFirst()) {
            do {
                categories.put(cursor.getInt(0), cursor.getString(titleRow));
            } while(cursor.moveToNext());
        }
        return categories;
    }

    private Cursor getCursorForQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }


}
