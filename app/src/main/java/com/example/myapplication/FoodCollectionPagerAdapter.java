package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class FoodCollectionPagerAdapter extends FragmentStatePagerAdapter {
    private Map<String, ArrayList<String>> foods;
    private List<String> dishes;
    private String restID;

    public FoodCollectionPagerAdapter(FragmentManager fm, Map<String, ArrayList<String>> foods, String restID) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.foods = foods;
        this.restID = restID;
        this.dishes = new ArrayList<>(foods.keySet());
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new FoodObjectFragment();
        Bundle args = new Bundle();

        // Passa il ristorante
        args.putString("restID", restID);

        // Passa la lista di cibi
        String dishName = dishes.get(i);
        args.putStringArrayList("foods", foods.get(dishName));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        // lunghezza della lista
        return foods.size();
    }

    @Override
    public CharSequence getPageTitle(int i) {
        // nome della portata
        return dishes.get(i);
    }
}
