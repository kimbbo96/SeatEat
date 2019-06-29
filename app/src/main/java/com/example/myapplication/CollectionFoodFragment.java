package com.example.myapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CollectionFoodFragment extends Fragment {
    // When requested, this adapter returns a FoodObjectFragment,
    // representing an object in the collection.
    FoodCollectionPagerAdapter demoCollectionPagerAdapter;
    ViewPager viewPager;
    Map<String, ArrayList<String>> foods = new TreeMap<>();
    String restID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_tab_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        demoCollectionPagerAdapter = new FoodCollectionPagerAdapter(getChildFragmentManager(), foods, restID);
        viewPager = view.findViewById(R.id.pager_food);
        viewPager.setAdapter(demoCollectionPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_food);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        restID = args.getString("restID");

        List<String> dishes = args.getStringArrayList("dishes");
        for (String dish : dishes) {
            foods.put(dish, args.getStringArrayList(dish));
        }
    }
}
