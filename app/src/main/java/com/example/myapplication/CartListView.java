package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.myapplication.utils.Cart;

import java.util.Formatter;
import java.util.List;
import java.util.Locale;


public class CartListView extends ArrayAdapter<String> {
    private Activity context;
    private List<Cart.CartFood> foods;
    private boolean old;

    public CartListView(Activity context, List<Cart.CartFood> foods, boolean old) {
        super(context, R.layout.activity_scrolling_restaurant, getNames(foods));

        this.context = context;
        this.foods = foods;
        this.old = old;
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
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.fcName.setText(foods.get(position).getName());
        viewHolder.fcNote.setText(foods.get(position).getNote());
        viewHolder.fcPrice.setText(new Formatter().format(
                Locale.ITALIAN, "x %d   %.2f€",
                foods.get(position).getQuantity(), foods.get(position).getPrice()).toString());

        if (old) {
            viewHolder.addFCB.setVisibility(View.GONE);
            viewHolder.removeFCB.setVisibility(View.GONE);
        }

        return r;
    }
    class  ViewHolder{
        TextView fcName;
        TextView fcNote;
        TextView fcPrice;
        ImageButton addFCB;
        ImageButton removeFCB;

        ViewHolder(View v){
            fcName = v.findViewById(R.id.foodCartName);
            fcNote = v.findViewById(R.id.foodCartNote);
            fcPrice = v.findViewById(R.id.foodCartPrice);
            addFCB = v.findViewById(R.id.addFoodCartButton);
            removeFCB = v.findViewById(R.id.removeFoodCartButton);
        }
    }
}
