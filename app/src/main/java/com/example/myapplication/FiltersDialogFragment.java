package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.MyLocation;

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

            List<Restaurant> newRests = filterRestaurants(MenuRest.restaurants, filterDistanceView, filterTypologyView);
            MenuRest.fillList(this.getActivity(), newRests);
            this.dismiss();
        });

        return filterDialogueView;
    }

    private List<Restaurant> filterRestaurants(List<Restaurant> restaurants, ListView distance, ListView typology) {
        List<Restaurant> newRests = new ArrayList<>();
        int distancePosition = distance.getCheckedItemPosition();
        SparseBooleanArray typologiesPositions = typology.getCheckedItemPositions();
        boolean hasPos = distancePosition != AdapterView.INVALID_POSITION;
        boolean hasType = typologiesPositions.size() != 0;
        Location here = null;
        Location targetLocation = new Location("");
        double checkedDistance = -1;
        List<String> checkedTypologies = new ArrayList<>();

        if (!hasPos && !hasType) {
            return restaurants;
        }

        if (hasPos) {
            System.out.println("distance str: " + distances[distancePosition]);
            Double[] intDistances = {1000d, 5000d, 10000d, Double.POSITIVE_INFINITY};
            checkedDistance = intDistances[distancePosition];
            System.out.println("distance: " + distances[distancePosition]);
            System.out.println("distance double: " + checkedDistance);

//            MyLocation myLoc = new MyLocation(getContext());
//            Location here = myLoc.getMyLocation();
            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            here = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            System.out.println("position: " + here);
        }

        if (hasType) {
            System.out.println("typologies size: " + typologiesPositions.size());
            for (int i = 0; i < typologies.length; i++) {
                if (typologiesPositions.get(i)) {
                    checkedTypologies.add(typologies[i]);
                }
            }
            System.out.println("typologies: " + checkedTypologies);
        }

        for (Restaurant r : restaurants) {
            boolean addPos = true;
            boolean addType = true;

            if (hasType) {
                String[] restTyp = r.getRESTAURANT_TYPOLOGY().split(",");
                int typCount = restTyp.length;

                for (String s : restTyp) {
                    s = s.trim();
                    if (! checkedTypologies.contains(s)) {
                        System.out.println("yes" + r);
                        typCount--;
                    }
                }
                if (typCount == 0)
                    addType = false;
            }

            if (hasPos) {
                targetLocation.setLatitude(r.getRESTAURANT_POSITION()[0]);
                targetLocation.setLongitude(r.getRESTAURANT_POSITION()[1]);
                float distanceInMeters =  targetLocation.distanceTo(here);

                if (distanceInMeters > checkedDistance) {
                    addPos = false;
                }
            }

            if (addType && addPos) {
                newRests.add(r);
            }
        }
        System.out.println(newRests);
        return newRests;
    }
}