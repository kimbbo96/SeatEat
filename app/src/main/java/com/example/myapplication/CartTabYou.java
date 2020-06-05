package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.myapplication.db_obj.Food;
import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.utils.Utils.justifyListViewHeight;

public class CartTabYou extends Fragment {
    private CartActivity activity;
    private Cart cart;
    private String userId;
    private String restId;
    private boolean created = false;

    public CartTabYou(CartActivity activity, String restId) {
        super();
        this.activity = activity;
        this.cart = activity.cart;
        this.restId = restId;
        System.out.println("creo CartTabYou");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        System.out.println("CARRELLO ATTUALE: " + cart);
        return inflater.inflate(R.layout.content_cart_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = activity.getSharedPreferences("loginref", MODE_PRIVATE);
        userId = preferences.getString("nome", "");
        created = true;

        fillFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillFragment();
        System.out.println("CartTabYou ONRESUME");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("destroyed tabYou");
        created = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        System.out.println("destroyed view tabYou");
        created = false;
    }

    private void fillFragment() {
        cart.load();

        TextView twTotal = activity.findViewById(R.id.tw_total_cart_you);
        twTotal.setText(new Formatter().format(Locale.ITALIAN, "Il tuo totale:   %.2f€", cart.getTotal(userId)).toString());

        ListView listView = activity.findViewById(R.id.list_view_cart_you);
        int ordNum = cart.getOrdNum();
        System.out.println("ordNum: " + ordNum);
        List<Cart.CartFood> foods = cart.getCartFoods(ordNum, userId);
        System.out.println("foods ora-tu: " + foods);
        CartListView customListView = new CartListView(activity, foods, false, true);

        listView.setAdapter(customListView);
        justifyListViewHeight(listView);

        ProgressBar progressBarCart = activity.findViewById(R.id.progressBar_cart_you);
        progressBarCart.setVisibility(View.GONE);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(activity, FoodDetail.class);
            Cart.CartFood cf = foods.get(i);
            Food food = new Food(cf.getId(), cf.getName(), cf.getShortDescr(), cf.getLongDescr(), "", cf.getPrice(), cf.getImage());
            intent.putExtra("Food", food); // passo l'oggetto cibo
            intent.putExtra("Quantity", cf.getQuantity());
            intent.putExtra("Note", foods.get(i).getNote());
            intent.putExtra("Mode", "edit");
            activity.startActivity(intent);
        });
    }
}
