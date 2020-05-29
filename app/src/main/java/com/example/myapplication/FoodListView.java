package com.example.myapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.db_obj.Food;

import static android.content.Context.MODE_PRIVATE;

class FoodListView extends ArrayAdapter<String> {
    private Activity context;
    private Food[] foods;
    private String[] foodName;
    private String[] foodDesc;
    private Double[] foodPrice;
    private String[] foodImage;
    private String[] foodId;
    private String restId;
    private Cart cart;
    private String userId;
    private String restIdPref;

    private final String path_base = "https://seateat-be.herokuapp.com";


    public FoodListView(Activity context, Food[] foods, String restId) {
        super(context, R.layout.activity_scrolling_restaurant, getNames(foods));

        this.cart = new Cart(context);
        SharedPreferences preferencesLogin = context.getSharedPreferences("loginref", MODE_PRIVATE);
        this.userId = preferencesLogin.getString("nome", "");
        SharedPreferences preferencesRest = context.getSharedPreferences("infoRes", MODE_PRIVATE);
        this.restIdPref = preferencesRest.getString("ID","");

        this.foods = foods;
        this.restId = restId;
        String[] foodName = new String[foods.length];
        String[] foodDesc = new String[foods.length];
        String[] foodImage = new String[foods.length];
        Double[] foodPrice = new Double[foods.length];
        String[] foodId = new String[foods.length];

        for (int i = 0; i < foods.length; i++) {
            System.out.println(foods[i]);

            foodName[i] = foods[i].getFOOD_TITLE();
            foodDesc[i] = foods[i].getFOOD_SHORT_DESCR();
            foodImage[i] = foods[i].getFOOD_IMAGE();
            foodPrice[i] = foods[i].getFOOD_PRICE();
            foodId[i] = foods[i].getFOOD_ID();
        }

        this.context = context;
        this.foodName = foodName;
        this.foodDesc = foodDesc;
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
        this.foodId = foodId;
    }

    private static String[] getNames(Food[] foods) {
        String[] restNames = new String[foods.length];
        for (int i = 0; i < foods.length; i++) {
            restNames[i] = foods[i].getFOOD_TITLE();
        }
        return restNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View f = convertView;
        ViewHolder viewHolder = null;
        if (f == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            f = layoutInflater.inflate(R.layout.activity_food_scrolling,
                    parent,false);
            viewHolder = new ViewHolder(f, parent);
            f.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) f.getTag();
        }

        viewHolder.tvw1.setText(foodName[position]);
        viewHolder.tvw2.setText(foodDesc[position]);
        viewHolder.tvw3.setText(foodPrice[position] + "€");
        viewHolder.counter.setText("");

        /* for the image --- online */
        Glide.with(context)
                .load(Uri.parse(path_base + "/resources/menus/" + restId + "/" + foodImage[position]))
                .into(viewHolder.ivw);

        final ViewHolder vh = viewHolder;

        ImageButton addIB = viewHolder.addButton;
        ImageButton remIB = viewHolder.removeButton;

        cart.load();

        final int quantity = 0;
        int[] counter = {quantity};

        System.out.println("rest ids: " + restId + " - " + restIdPref);

        if (restId.equals(restIdPref)) {
            // TODO manage cart (add)
            addIB.setOnClickListener(view -> {
                cart.load();
                Food food = foods[position];
                cart.addCartFood(food.getFOOD_ID(), food.getFOOD_TITLE(), food.getFOOD_PRICE(), userId, "",
                        food.getFOOD_SHORT_DESCR(), food.getFOOD_LONG_DESCR(), food.getFOOD_IMAGE());
                cart.save();

                counter[0] += 1;
                vh.counter.setText("x"+counter[0]);
                vh.cartButton.setText("Totale: " + cart.getTotal() + "€");
            });

            // TODO manage cart (remove)
            remIB.setOnClickListener(view -> {
                cart.load();
                cart.removeCartFood(foodId[position], userId, "");
                cart.save();

                counter[0] -= 1;

                if (counter[0] <= 0) {
                    vh.counter.setText("");
                    counter[0] = 0;
                }
                else {
                    vh.counter.setText("x"+counter[0]);
                }
                vh.cartButton.setText("Totale: " + cart.getTotal() + "€");
            });
        } else {
            vh.counter.setVisibility(View.GONE);
            addIB.setVisibility(View.GONE);
            remIB.setVisibility(View.GONE);
        }

        return f;
    }

    @Override
    public boolean isEnabled(int position) {
        return restId.equals(restIdPref);
    }

    class  ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        ImageView ivw;
        ImageButton addButton;
        ImageButton removeButton;
        ExtendedFloatingActionButton cartButton;
        TextView counter;

        ViewHolder(View v, ViewGroup parent){
            tvw1 = v.findViewById(R.id.foodName);
            tvw2 = v.findViewById(R.id.foodDes);
            tvw3 = v.findViewById(R.id.foodPrice);
            ivw = v.findViewById(R.id.foodImg);
            addButton = v.findViewById(R.id.addFoodButton);
            removeButton = v.findViewById(R.id.removeFoodButton);
            counter = v.findViewById(R.id.foodCounter);
            ViewGroup newParent = (ViewGroup) parent.getParent().getParent().getParent().getParent().getParent();
//            System.out.println("parent: " + newParent);
            cartButton = newParent.findViewById(R.id.fab_food);
        }
    }
}
