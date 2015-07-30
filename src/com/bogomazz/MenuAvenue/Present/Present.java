package com.bogomazz.MenuAvenue.Present;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bogomazz.MenuAvenue.Database;
import com.bogomazz.MenuAvenue.Item;
import com.bogomazz.MenuAvenue.Menu.Menu;
import com.bogomazz.MenuAvenue.R;
import com.bogomazz.MenuAvenue.RootActivity;
import org.w3c.dom.Text;

/**
 * Created by andrey on 9/16/14.
 */
public class Present extends Fragment {
    final String PREFS_NAME = "preferences";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.present_activity, container, false);

        setUpActionBar();
        setUpList(rootView);

        return rootView;
    }
    private void setUpActionBar() {
        TextView title = (TextView) getActivity().findViewById(R.id.title);
        Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(), "RobotoCondensed-Bold.ttf");

        title.setTypeface(titleFont);
        title.setText(getResources().getString(R.string.present_bar));
    }
    private void setUpList(View rootView) {

        PresentListAdapter adapter = new PresentListAdapter(getActivity().getApplicationContext(), Item.items.get(Item.CATEGORY_PRESENT));
        ListView presentsList = (ListView) rootView.findViewById(R.id.listView);

        presentsList.setAdapter(adapter);

        presentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Toast.makeText(getApplicationContext(), "works", Toast.LENGTH_LONG).show();
            }
        });
    }



}
