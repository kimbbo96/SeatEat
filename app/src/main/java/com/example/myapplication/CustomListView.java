package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CustomListView extends ArrayAdapter<String> {
    private String[] restName;
    private String[] restDesc;
    private Integer[] imgId;
    private Activity context;
    private Float[] rate;
    public CustomListView(Activity context, String[] resName, String[] restDesc,
                          Integer[] imgId, Float[] rate) {
        super(context, R.layout.activity_scrolling_restaurant,resName);
        this.context = context;
        this.imgId = imgId;
        this.restDesc = restDesc;
        this.restName = resName;
        this.rate = rate;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.activity_scrolling_restaurant,
                    null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) r.getTag();
        }
        viewHolder.ivw.setImageResource(imgId[position]);
        viewHolder.tvw1.setText(restName[position]);
        viewHolder.tvw2.setText(restDesc[position]);
        viewHolder.rb.setRating(rate[position]);

        return r;
    }
    class  ViewHolder{
        TextView tvw1;
        TextView tvw2;
        RatingBar rb;
        ImageView ivw;
        ViewHolder(View v){
            tvw1 = v.findViewById(R.id.resName);
            tvw2 = v.findViewById(R.id.resDes);
            ivw = v.findViewById(R.id.imageView2);
            rb =  v.findViewById(R.id.ratingBar);
            rb.setNumStars(5);
            System.out.println((rb.getMax()));
            rb.setMax(4);

        }
    }
}
