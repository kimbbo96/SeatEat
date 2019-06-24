package com.example.myapplication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

class FoodListView extends ArrayAdapter<String> {
    private String[] foodName;
    private String[] foodDesc;
    private Activity context;
    private Float[] foodPrice;
    public FoodListView(Activity context, String[] foodName, String[] foodDesc, Float[] foodPrice) {
        super(context, R.layout.activity_scrolling_restaurant,foodName);
        this.context = context;
        this.foodDesc = foodDesc;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.activity_food_scrolling,
                    null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) r.getTag();
        }
        viewHolder.tvw1.setText(foodName[position]);
        viewHolder.tvw2.setText(foodDesc[position]);
        viewHolder.tvw3.setText(foodPrice[position] + "€");

        return r;
    }
    class  ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        ViewHolder(View v){
            tvw1 = v.findViewById(R.id.foodName);
            tvw2 = v.findViewById(R.id.foodDes);
            tvw3 = v.findViewById(R.id.foodPrice);
        }
    }
}
