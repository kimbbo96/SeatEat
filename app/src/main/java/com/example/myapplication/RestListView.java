package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.db_obj.Restaurant;

import java.util.Formatter;
import java.util.Locale;

public class RestListView extends ArrayAdapter<String> {
    private String[] restName;
    private String[] restDesc;
    private String[] imgId;
    private Float[] restRate;
    private Float[] restDist;
    private Activity context;
    private String path_base = "https://seateat-be.herokuapp.com";

    public RestListView(Activity context, String[] restName, String[] restDesc,
                        String[] imgId, Float[] rate) {
        super(context, R.layout.activity_scrolling_restaurant, restName);
        this.context = context;
        this.imgId = imgId;
        this.restDesc = restDesc;
        this.restName = restName;
        this.restRate = rate;
    }

    public RestListView(Activity context, Restaurant[] restaurants) {
        super(context, R.layout.activity_scrolling_restaurant, getNames(restaurants));
        String[] restName = new String[restaurants.length];
        String[] restDesc = new String[restaurants.length];
        String[] imgId = new String[restaurants.length];
        Float[] rate = new Float[restaurants.length];
        Float[] distance = new Float[restaurants.length];

        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            MainActivity.here = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        Location targetLocation = new Location("");

        for (int i = 0; i < restaurants.length; i++) {
            System.out.println(restaurants[i]);

            restName[i] = restaurants[i].getRESTAURANT_TITLE();
            restDesc[i] = restaurants[i].getRESTAURANT_TYPOLOGY();
            imgId[i] = restaurants[i].getRESTAURANT_IMAGE();
            rate[i] = restaurants[i].getRESTAURANT_RATING();

            targetLocation.setLatitude(restaurants[i].getRESTAURANT_POSITION()[0]);
            targetLocation.setLongitude(restaurants[i].getRESTAURANT_POSITION()[1]);
            distance[i] = targetLocation.distanceTo(MainActivity.here);
        }

        this.context = context;
        this.imgId = imgId;
        this.restDesc = restDesc;
        this.restName = restName;
        this.restRate = rate;
        this.restDist = distance;
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
        viewHolder.tvw3.setText(new Formatter().format(Locale.ITALIAN, "%.3f km", restDist[position]/1000f).toString());
        viewHolder.rb.setRating(restRate[position]);

        return r;
    }
    class  ViewHolder{
        TextView tvw1;
        TextView tvw2;
        TextView tvw3;
        RatingBar rb;
        ImageView ivw;

        ViewHolder(View v){
            tvw1 = v.findViewById(R.id.resName);
            tvw2 = v.findViewById(R.id.resDes);
            tvw3 = v.findViewById(R.id.resDist);
            ivw = v.findViewById(R.id.imageView2);
            rb =  v.findViewById(R.id.ratingBar);
            rb.setNumStars(5);
            System.out.println((rb.getMax()));
            rb.setMax(4);

        }
    }
}

