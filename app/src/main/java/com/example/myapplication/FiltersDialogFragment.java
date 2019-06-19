package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FiltersDialogFragment extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View filterDialogueView = inflater.inflate(R.layout.filters_dialog, container);

        Context context = getContext();
        System.out.println("context = " + context);

        ListView filterDistanceView = filterDialogueView.findViewById(R.id.filter_distance);
        List<String> dataListDistance = new ArrayList<String>();
        dataListDistance.add("1km");
        dataListDistance.add("5km");
        dataListDistance.add("10km");
        dataListDistance.add("10km +");
        ArrayAdapter<String> arrayAdapterDistance = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, dataListDistance);
        filterDistanceView.setAdapter(arrayAdapterDistance);
        filterDistanceView.setOnItemClickListener((adapterView, view, index, l) -> {
            Object clickItemObj = adapterView.getAdapter().getItem(index);
            Toast.makeText(context, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
        });

        ListView filterTypologyView = filterDialogueView.findViewById(R.id.filter_typology);
        List<String> dataListTypology = new ArrayList<String>();
        dataListTypology.add("Pizzeria");
        dataListTypology.add("Ristorante");
        dataListTypology.add("Paninoteca");
        dataListTypology.add("Gelateria");
        ArrayAdapter<String> arrayAdapterTypology = new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, dataListTypology);
        filterTypologyView.setAdapter(arrayAdapterTypology);
        filterTypologyView.setOnItemClickListener((adapterView, view, index, l) -> {
            Object clickItemObj = adapterView.getAdapter().getItem(index);
            Toast.makeText(context, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
        });

        FloatingActionButton fab = filterDialogueView.findViewById(R.id.filter_ok);
        fab.setOnClickListener(view -> Snackbar.make(view, "Filtriamo cose", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        return filterDialogueView;
    }
}