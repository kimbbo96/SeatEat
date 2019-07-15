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

class CartTabYou extends Fragment {
    private CartActivity activity;
    private Cart cart;
    private String userId;
    private String restId;
    private boolean created = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && created) {
                String content = intent.getStringExtra("content");
                System.out.println(content + " YOU");
                if (content.equals("new CartUser"))
                    setParticipants();
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

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(activity);
        lbm.registerReceiver(receiver, new IntentFilter("add_user"));
        lbm.registerReceiver(receiverOrdNum, new IntentFilter("new_ord_num"));
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

        ListView listViewOld = activity.findViewById(R.id.list_view_cart_old_you);
        List<Cart.CartFood> foodsOld = cart.getOldCartFoods(ordNum, userId);
        System.out.println("foods prima-tu: " + foodsOld);
        CartListView customListViewOld = new CartListView(activity, foodsOld, true, true);

        listView.setAdapter(customListView);
        justifyListViewHeight(listView);
        listViewOld.setAdapter(customListViewOld);
        justifyListViewHeight(listViewOld);

        ProgressBar progressBarCart = activity.findViewById(R.id.progressBar_cart_you);
        progressBarCart.setVisibility(View.GONE);

//        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
//        boolean isCapotavola = preferences.getBoolean("isCapotavola",false);
//        FloatingActionButton fabCart = activity.findViewById(R.id.fab_cart_you);
//        FloatingActionButton fabCheckout = activity.findViewById(R.id.fab_checkout_you);
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

        setParticipants();
    }

    private void setParticipants() {
        SharedPreferences preferencesRest = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
        String restIdPref = preferencesRest.getString("ID","");
        TextView participantsTvYou = activity.findViewById(R.id.fellowship_cart_you);

        if (restId.equals(restIdPref)) {
            cart.load();
            String fellowship = cart.getCartUsersNames();
            System.out.println("FELLOWSHIP CARTTABYOU " + fellowship);
            if (fellowship == null) {
                participantsTvYou.setText("Partecipanti: tu");
            } else {
                participantsTvYou.setText("Partecipanti: " + fellowship);
            }
        } else {
            participantsTvYou.setVisibility(View.INVISIBLE);
        }
    }

//    public void hideButtons() {
//        FloatingActionButton fabCart = activity.findViewById(R.id.fab_cart_all);
//        FloatingActionButton fabCheckout = activity.findViewById(R.id.fab_checkout_all);
//        fabCart.setVisibility(View.GONE);
//        fabCheckout.setVisibility(View.GONE);
//    }
//
//    public void showButtons() {
//        FloatingActionButton fabCart = activity.findViewById(R.id.fab_cart_all);
//        FloatingActionButton fabCheckout = activity.findViewById(R.id.fab_checkout_all);
//        fabCart.setVisibility(View.VISIBLE);
//        fabCheckout.setVisibility(View.VISIBLE);
//    }
}
