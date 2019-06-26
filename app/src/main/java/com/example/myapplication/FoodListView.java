package com.example.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.db_obj.Food;

import java.lang.reflect.Field;

class FoodListView extends ArrayAdapter<String> {
    private Activity context;
    private String[] foodName;
    private String[] foodDesc;
    private Double[] foodPrice;
    private String[] foodImage;
    private String restId;

    private String path_base = "https://seateat-be.herokuapp.com";

    public FoodListView(Activity context, String[] foodName, String[] foodDesc, Double[] foodPrice) {
        super(context, R.layout.activity_scrolling_restaurant,foodName);
        this.context = context;
        this.foodDesc = foodDesc;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    public FoodListView(Activity context, Food[] foods, String restId) {
        super(context, R.layout.activity_scrolling_restaurant, getNames(foods));
        this.restId = restId;
        String[] foodName = new String[foods.length];
        String[] foodDesc = new String[foods.length];
        String[] foodImage = new String[foods.length];
        Double[] foodPrice = new Double[foods.length];

        for (int i = 0; i < foods.length; i++) {
            System.out.println(foods[i]);

            foodName[i] = foods[i].getFOOD_TITLE();
            foodDesc[i] = foods[i].getFOOD_SHORT_DESCR();
            foodImage[i] = foods[i].getFOOD_IMAGE();
            foodPrice[i] = foods[i].getFOOD_PRICE();
        }

        this.context = context;
        this.foodName = foodName;
        this.foodDesc = foodDesc;
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
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
                    null,true);
            viewHolder = new ViewHolder(f);
            f.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) f.getTag();
        }

        viewHolder.tvw1.setText(foodName[position]);
        viewHolder.tvw2.setText(foodDesc[position]);
        viewHolder.tvw3.setText(foodPrice[position] + "€");

        /* for the image --- offline */
//        int imgId = -1;
//        try {
//            Class res = R.drawable.class;
//            Field field = res.getField(foodImage[position]);
//            imgId = field.getInt(null);
//        }
//        catch (Exception e) {
//            System.out.println("Restaurant image not found: " + foodImage[position] + ", " + imgId + "\n" + e);
//        }
//        viewHolder.ivw.setImageResource(imgId);

        /* for the image --- online */
        Glide.with(context)
                .load(Uri.parse(path_base + "/resources/menus/" + restId + "/" + foodImage[position]))
                .into(viewHolder.ivw);

        return f;
    }

    class  ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        ImageView ivw;
        ViewHolder(View v){
            tvw1 = v.findViewById(R.id.foodName);
            tvw2 = v.findViewById(R.id.foodDes);
            tvw3 = v.findViewById(R.id.foodPrice);
            ivw = v.findViewById(R.id.foodImg);
        }
    }
}
