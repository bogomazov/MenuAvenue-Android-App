package com.bogomazz.MenuAvenue.Menu.ItemList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.Item.ItemActivity;
import com.bogomazz.MenuAvenue.Menu.MenuListAdapter;
import com.bogomazz.MenuAvenue.R;

/**
 * Created by andrey on 11/1/14.
 */
public class ItemsListFragment extends Fragment {
    private int categoryId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_list_fragment, container, false);
        categoryId = getArguments().getInt("index") + 1;
        ListView itemsListView = (ListView) rootView.findViewById(R.id.fragmentListView);
        boolean isPizza = categoryId == Item.CATEGORY_PIZZA;
        final Item items[];
        if ( isPizza ) {
            items = Item.pizzasToDisplay;
        } else {
            items = Item.items.get(categoryId);
        }
        ItemsListAdapter adapter = new ItemsListAdapter(getActivity().getApplicationContext(), items, categoryId);

        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ItemActivity.class);
                intent.putExtra("categoryId", categoryId);
//                if (categoryId == Item.CATEGORY_PIZZA) {
                if (categoryId == Item.CATEGORY_PIZZA) {
                    intent.putExtra("itemIndexInArray", Item.getFirstPizzaFromPositionInList(position));
                }
//                } else {
//                    intent.putExtra("itemIndexInArray", position);
//                }
                ItemActivity.item = items[position];
                Log.d("Click", "some");
                startActivity(intent);
            }
        });
        itemsListView.setAdapter(adapter);


        return rootView;
    }
}
