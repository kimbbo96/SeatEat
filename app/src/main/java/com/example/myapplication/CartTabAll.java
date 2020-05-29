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

public class CartTabAll extends Fragment {
    private CartActivity activity;
    private Cart cart;
    private String restId;
    private boolean created = false;

    private BroadcastReceiver receiverUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && created) {
                String content = intent.getStringExtra("content");
                System.out.println(content + " ALL");
            }
        }
    };

    private BroadcastReceiver receiverCart = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && created) {
                String content = intent.getStringExtra("content");
                System.out.println("BroadcastReceiver: " + content);
                if (content.equals("new Cart"))
                    fillFragment();
            } else {
                System.out.println("BroadcastReceiver FALSE: intent = " + intent + ", created = " + created);
            }
        }
    };

    private BroadcastReceiver receiverOrdNum = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && created) {
                String content = intent.getStringExtra("content");
                System.out.println("BroadcastReceiver: " + content);
                if (content.equals("new NumOrd"))
                    fillFragment();
            } else {
                System.out.println("BroadcastReceiver FALSE: intent = " + intent + ", created = " + created);
            }
        }
    };

    public CartTabAll(CartActivity activity, String restId) {
        super();
        this.activity = activity;
        this.cart = activity.cart;
        this.restId = restId;
        System.out.println("creo CartTabAll");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
        lbm.registerReceiver(receiverUser, new IntentFilter("add_user"));
        lbm.registerReceiver(receiverCart, new IntentFilter("update_cart"));
        lbm.registerReceiver(receiverOrdNum, new IntentFilter("new_ord_num"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        System.out.println("CARRELLO ATTUALE: " + cart);

        return inflater.inflate(R.layout.content_cart_all, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        created = true;
        fillFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillFragment();
        System.out.println("CartTabAll ONRESUME");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("destroyed tabAll");
        created = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        System.out.println("destroyed view tabAll");
        created = false;
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
        CartListView customListView = new CartListView(activity, foods, false, true);

        ListView listViewOld = activity.findViewById(R.id.list_view_cart_old_all);
        List<Cart.CartFood> foodsOld = cart.getOldCartFoods(ordNum);
        System.out.println("foods prima-tutti: " + foodsOld);
        CartListView customListViewOld = new CartListView(activity, foodsOld, true, true);

        listView.setAdapter(customListView);
        justifyListViewHeight(listView);
        listViewOld.setAdapter(customListViewOld);
        justifyListViewHeight(listViewOld);

        ProgressBar progressBarCart = activity.findViewById(R.id.progressBar_cart_all);
        progressBarCart.setVisibility(View.GONE);

//        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
//        boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
//        FloatingActionButton fabCart = activity.findViewById(R.id.fab_cart_all);
//        FloatingActionButton fabCheckout = activity.findViewById(R.id.fab_checkout_all);
//        if (isCapotavola) {
//            fabCart.setOnClickListener(activity.new FabCartClickListener(cart));
//            fabCheckout.setOnClickListener(activity.new FabCheckoutClickListener());
//        } else {
//            fabCart.setEnabled(false);
//            fabCheckout.setEnabled(false);
//        }

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
