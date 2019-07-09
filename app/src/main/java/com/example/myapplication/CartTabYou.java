package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.example.myapplication.utils.Utils.justifyListViewHeight;

class CartTabYou extends Fragment {
    private CartActivity activity;
    private Cart cart;

    public CartTabYou(CartActivity activity) {
        super();
        this.activity = activity;
        this.cart = activity.cart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        cart.load();
        System.out.println("CARRELLO ATTUALE: " + cart);
        return inflater.inflate(R.layout.content_cart_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView twTotal = activity.findViewById(R.id.tw_total_cart_you);
        twTotal.setText(new Formatter().format(Locale.ITALIAN, "Il tuo totale:   %.2f€", cart.getTotal()).toString());

        SharedPreferences preferences = activity.getSharedPreferences("loginref", Context.MODE_PRIVATE);
        String userId = preferences.getString("nome", "");

        ListView listView = activity.findViewById(R.id.list_view_cart_you);
        int ordNum = cart.getOrdNum();
        System.out.println("ordNum: " + ordNum);
        List<Cart.CartFood> foods = cart.getCartFoods(ordNum, userId);
        System.out.println("foods ora-tu: " + foods);
        CartListView customListView = new CartListView(activity, foods, false);

        ListView listViewOld = activity.findViewById(R.id.list_view_cart_old_you);
        List<Cart.CartFood> foodsOld = cart.getOldCartFoods(ordNum, userId);
        System.out.println("foods prima-tu: " + foodsOld);
        CartListView customListViewOld = new CartListView(activity, foodsOld, true);

        listView.setAdapter(customListView);
        justifyListViewHeight(listView);
        listViewOld.setAdapter(customListViewOld);
        justifyListViewHeight(listViewOld);

        ProgressBar progressBarCart = activity.findViewById(R.id.progressBar_cart_you);
        progressBarCart.setVisibility(View.GONE);

        ExtendedFloatingActionButton fab = activity.findViewById(R.id.fab_cart_you);
        fab.setOnClickListener(activity.new FabCartClickListener(cart));

//        final List<Restaurant> resList = new ArrayList<>(Arrays.asList(restaurants));
//        listView.setOnItemClickListener((adapterView, view, i, l) -> {
//            System.out.println("hai clikkato "+i);
//            Intent intent = new Intent(activity, ResDetail.class);
//            intent.putExtra("Restaurant", restaurants.get(i)); // passo l'oggetto ristorante
//            activity.startActivity(intent);
//        });
    }
}