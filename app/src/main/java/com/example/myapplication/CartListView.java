package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.utils.Cart;
import com.google.android.material.snackbar.Snackbar;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class CartListView extends ArrayAdapter<String> {
    private Activity context;
    private List<Cart.CartFood> foods;
    private boolean old;
    private Cart cart;
    private String userId;

    public CartListView(Activity context, List<Cart.CartFood> foods, boolean old) {
        super(context, R.layout.activity_scrolling_restaurant, getNames(foods));

        this.context = context;
        this.foods = foods;
        this.old = old;
        this.cart = new Cart(context);

        SharedPreferences preferences = context.getSharedPreferences("loginref", MODE_PRIVATE);
        this.userId = preferences.getString("nome", "");
    }

    private static String[] getNames(List<Cart.CartFood> foods) {
        String[] foodNames = new String[foods.size()];
        for (int i = 0; i < foods.size(); i++) {
            foodNames[i] = foods.get(i).getName();
        }
        return foodNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder;
        if (r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.activity_cart_scrolling,
                    parent,false);
            viewHolder = new ViewHolder(r, parent);
            r.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) r.getTag();
        }

        Cart.CartFood cf = foods.get(position);
        viewHolder.fcName.setText(cf.getName());
        viewHolder.fcNote.setText(cf.getNote());
        viewHolder.fcPrice.setText(new Formatter().format(
                Locale.ITALIAN, "x %d   %.2f€",
                foods.get(position).getQuantity(), foods.get(position).getPrice()).toString());

        if (old) {
            viewHolder.addFCB.setVisibility(View.GONE);
            viewHolder.removeFCB.setVisibility(View.GONE);
        } else {
            // TODO manage cart (add)
            viewHolder.addFCB.setOnClickListener(view -> {
                cart.load();
                Cart.CartFood cartFood = cart.addCartFood(cf.getId(), cf.getName(), cf.getPrice(), userId, cf.getNote());
                cart.save();
                System.out.println("added food from cart: " + cartFood);
                viewHolder.fcPrice.setText(new Formatter().format(
                        Locale.ITALIAN, "x %d   %.2f€",
                        cartFood.getQuantity(), cartFood.getPrice()).toString());
                viewHolder.totalTvYou.setText(new Formatter().format(Locale.ITALIAN, "Il tuo totale:   %.2f€", cart.getTotal(userId)).toString());
                viewHolder.totalTvAll.setText(new Formatter().format(Locale.ITALIAN, "Totale:   %.2f€", cart.getTotal()).toString());
            });

            // TODO manage cart (remove)
            viewHolder.removeFCB.setOnClickListener(view -> {
                cart.load();
                Cart.CartFood cartFood = cart.removeCartFood(cf.getId(), userId, cf.getNote());
                cart.save();
                System.out.println("removed food from cart: " + cartFood);
                viewHolder.totalTvYou.setText(new Formatter().format(Locale.ITALIAN, "Il tuo totale:   %.2f€", cart.getTotal(userId)).toString());
                viewHolder.totalTvAll.setText(new Formatter().format(Locale.ITALIAN, "Totale:   %.2f€", cart.getTotal()).toString());
                viewHolder.fcPrice.setText(new Formatter().format(
                        Locale.ITALIAN, "x %d   %.2f€",
                        cartFood.getQuantity(), cartFood.getPrice()).toString());
            });
        }
        return r;
    }

    @Override
    public boolean isEnabled(int position) {
        return !old;
    }

    class  ViewHolder{
        TextView fcName;
        TextView fcNote;
        TextView fcPrice;
        ImageButton addFCB;
        ImageButton removeFCB;
        TextView totalTvYou;
        TextView totalTvAll;

        ViewHolder(View v, ViewGroup parent){
            fcName = v.findViewById(R.id.foodCartName);
            fcNote = v.findViewById(R.id.foodCartNote);
            fcPrice = v.findViewById(R.id.foodCartPrice);
            addFCB = v.findViewById(R.id.addFoodCartButton);
            removeFCB = v.findViewById(R.id.removeFoodCartButton);

            ViewGroup newParent = (ViewGroup) parent.getParent().getParent().getParent().getParent().getParent();
            totalTvYou = newParent.findViewById(R.id.tw_total_cart_you);
            totalTvAll = newParent.findViewById(R.id.tw_total_cart_all);
        }
    }
}
