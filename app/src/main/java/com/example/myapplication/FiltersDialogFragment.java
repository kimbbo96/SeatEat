package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.db_obj.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FiltersDialogFragment extends BottomSheetDialogFragment {
    private final String[] distances = {"1km", "5km", "10km", "10km +"};
    private final String[] typologies = {"Pizzeria", "Italiano", "Paninoteca", "Cinese", "Giapponese"};

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View filterDialogueView = inflater.inflate(R.layout.filters_dialog, container);

        Context context = getContext();
        System.out.println("context = " + context);

        ListView filterDistanceView = filterDialogueView.findViewById(R.id.filter_distance);
        List<String> dataListDistance = new ArrayList<>(Arrays.asList(distances));
        ArrayAdapter<String> arrayAdapterDistance = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, dataListDistance);
        filterDistanceView.setAdapter(arrayAdapterDistance);

//        filterDistanceView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, dataListDistance) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView text = view.findViewById(android.R.id.text1);
//                text.setTextColor(Color.BLACK);
//                return view;
//            }
//        });

        filterDistanceView.setOnItemClickListener((adapterView, view, index, l) -> {
            Object clickItemObj = adapterView.getAdapter().getItem(index);
            Toast.makeText(context, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
        });

        ListView filterTypologyView = filterDialogueView.findViewById(R.id.filter_typology);
        List<String> dataListTypology = new ArrayList<>(Arrays.asList(typologies));
        ArrayAdapter<String> arrayAdapterTypology = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, dataListTypology);
        filterTypologyView.setAdapter(arrayAdapterTypology);
        filterTypologyView.setOnItemClickListener((adapterView, view, index, l) -> {
            Object clickItemObj = adapterView.getAdapter().getItem(index);
            Toast.makeText(context, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
        });

        FloatingActionButton fab = filterDialogueView.findViewById(R.id.filter_ok);
        fab.setOnClickListener(view -> {
            Snackbar.make(view, "Filtriamo cose", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Restaurant[] newRests = filterRestaurants(menu_rest.restaurants, filterDistanceView, filterTypologyView);
            menu_rest.fillList(this.getActivity(), newRests);
            this.dismiss();
        });

        return filterDialogueView;
    }

    private Restaurant[] filterRestaurants(Restaurant[] restaurants, ListView distance, ListView typology) {
        List<Restaurant> newRests = new ArrayList<>();
        SparseBooleanArray distancePositions = distance.getCheckedItemPositions();
        SparseBooleanArray typologiesPositions = typology.getCheckedItemPositions();

        if (distancePositions.size() == 0 && typologiesPositions.size() == 0) {
            return restaurants;
        }

        System.out.println("distances: " + distancePositions.size());
        List<String> checkedDistances = new ArrayList<>();
        for (int i = 0; i < distances.length; i++) {
            if (distancePositions.get(i)) {
                checkedDistances.add(distances[i]);
            }
        }
        System.out.println(checkedDistances);

        System.out.println("typologies size: " + typologiesPositions.size());
        List<String> checkedTypologies = new ArrayList<>();
        for (int i = 0; i < typologies.length; i++) {
            if (typologiesPositions.get(i)) {
                checkedTypologies.add(typologies[i]);
            }
        }
        System.out.println("typologies: " + checkedTypologies);

        for (Restaurant r : restaurants) {
            String[] restTyp = r.getRESTAURANT_TYPOLOGY().split(",");
            for (String s : restTyp) {
                s = s.trim();
                if (checkedTypologies.contains(s)) {
                    System.out.println("yes" + r);
                    newRests.add(r);
                }
            }
        }
        System.out.println(newRests);
        return newRests.toArray(new Restaurant[0]);
    }
}