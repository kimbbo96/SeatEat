package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.utils.Cart;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.content.Context.MODE_PRIVATE;

public class CollectionFoodFragment extends Fragment {
    // When requested, this adapter returns a FoodObjectFragment,
    // representing an object in the collection.
    private FoodCollectionPagerAdapter demoCollectionPagerAdapter;
    private ViewPager viewPager;
    private View view;
    private Cart cart;
    private Map<String, ArrayList<String>> foods = new TreeMap<>();
    private String restID;
    private String userID;
    private boolean created = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && view != null && created) {
                String content = intent.getStringExtra("content");
                if (content.equals("new CartUser"))
                    setParticipants(view);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cart = new Cart(getContext());
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        lbm.registerReceiver(receiver, new IntentFilter("add_user"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.food_tab_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.view = view;
        this.demoCollectionPagerAdapter = new FoodCollectionPagerAdapter(getChildFragmentManager(), foods, restID);
        this.viewPager = view.findViewById(R.id.pager_food);
        this.created = true;
        viewPager.setAdapter(demoCollectionPagerAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_food);
        tabLayout.setupWithViewPager(viewPager);

        setParticipants(view);
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        restID = args.getString("restID");
        userID = args.getString("userID");

        List<String> dishes = args.getStringArrayList("dishes");
        for (String dish : dishes) {
            foods.put(dish, args.getStringArrayList(dish));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setParticipants(view);
    }

    private void setParticipants(View view) {
        SharedPreferences preferencesRest = view.getContext().getSharedPreferences("infoRes", MODE_PRIVATE);
        String idRestPref = preferencesRest.getString("ID","");
        TextView fellowshipFood = view.findViewById(R.id.fellowship_food);

        if (restID.equals(idRestPref)) {
            cart.load();
            String fellowship = cart.getCartUsersNames();
            System.out.println("FELLOWSHIP COLLECTIONFOODFRAGMENT " + fellowship);
            if (fellowship == null) {
                fellowshipFood.setText("Partecipanti: tu");
            } else {
                fellowshipFood.setText("Partecipanti: " + fellowship);
            }
        } else {
            fellowshipFood.setVisibility(View.INVISIBLE);
        }
    }
}
