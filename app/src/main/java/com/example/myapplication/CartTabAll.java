package com.example.myapplication;

import android.content.Intent;
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

class CartTabAll extends Fragment {
    private CartActivity activity;
    private Cart cart;

    public CartTabAll(CartActivity activity) {
        super();
        this.activity = activity;
        this.cart = activity.cart;
        System.out.println("creo CartTabAll");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        System.out.println("CARRELLO ATTUALE: " + cart);
        return inflater.inflate(R.layout.content_cart_all, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillFragment();
        System.out.println("CartTabAll ONRESUME");
    }

    private void fillFragment() {
        cart.load();

        TextView twTotal = activity.findViewById(R.id.tw_total_cart_all);
        twTotal.setText(new Formatter().format(Locale.ITALIAN, "Totale:   %.2f€", cart.getTotal()).toString());

        ListView listView = activity.findViewById(R.id.list_view_cart_all);
        int ordNum = cart.getOrdNum();
        System.out.println("ordNum: " + ordNum);
        List<Cart.CartFood> foods = cart.getCartFoods(ordNum);
        System.out.println("foods ora-tutti: " + foods);
        CartListView customListView = new CartListView(activity, foods, false);

        ListView listViewOld = activity.findViewById(R.id.list_view_cart_old_all);
        List<Cart.CartFood> foodsOld = cart.getOldCartFoods(ordNum);
        System.out.println("foods prima-tutti: " + foodsOld);
        CartListView customListViewOld = new CartListView(activity, foodsOld, true);

        listView.setAdapter(customListView);
        justifyListViewHeight(listView);
        listViewOld.setAdapter(customListViewOld);
        justifyListViewHeight(listViewOld);

        ProgressBar progressBarCart = activity.findViewById(R.id.progressBar_cart_all);
        progressBarCart.setVisibility(View.GONE);

        ExtendedFloatingActionButton fab = activity.findViewById(R.id.fab_cart_all);
        fab.setOnClickListener(activity.new FabCartClickListener(cart));

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(activity, FoodDetail.class);
            intent.putExtra("Food", foods.get(i)); // passo l'oggetto cibo
            activity.startActivity(intent);
        });
    }
}
