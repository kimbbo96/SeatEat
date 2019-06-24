package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.myapplication.db_obj.Restaurant;

import java.lang.reflect.Field;

public class RestListView extends ArrayAdapter<String> {
    private String[] restName;
    private String[] restDesc;
    private String[] imgId;
    private Activity context;
    private Float[] rate;
    private String path_base = "https://seateat-be.herokuapp.com";

    public RestListView(Activity context, String[] restName, String[] restDesc,
                        String[] imgId, Float[] rate) {
        super(context, R.layout.activity_scrolling_restaurant, restName);
        this.context = context;
        this.imgId = imgId;
        this.restDesc = restDesc;
        this.restName = restName;
        this.rate = rate;
    }

    public RestListView(Activity context, Restaurant[] restaurants) {
        super(context, R.layout.activity_scrolling_restaurant, getNames(restaurants));
        String[] restName = new String[restaurants.length];
        String[] restDesc = new String[restaurants.length];
        String[] imgId = new String[restaurants.length];
        Float[] rate = new Float[restaurants.length];
        for (int i = 0; i < restaurants.length; i++) {
            System.out.println(restaurants[i]);

            restName[i] = restaurants[i].getRESTAURANT_TITLE();
            restDesc[i] = restaurants[i].getRESTAURANT_TYPOLOGY();

            imgId[i] = restaurants[i].getRESTAURANT_IMAGE();
            rate[i] = restaurants[i].getRESTAURANT_RATING();
        }
        this.context = context;
        this.imgId = imgId;
        this.restDesc = restDesc;
        this.restName = restName;
        this.rate = rate;
    }

    private static String[] getNames(Restaurant[] restaurants) {
        String[] restNames = new String[restaurants.length];
        for (int i = 0; i < restaurants.length; i++) {
            restNames[i] = restaurants[i].getRESTAURANT_TITLE();
        }
        return restNames;
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
        System.out.println("jjjj"+restName[position]);
        //viewHolder.ivw.setImageResource(imgId[position]);
        Glide.with(context)
                .load(Uri.parse(path_base+imgId[position]))
                .into(viewHolder.ivw);
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

