package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.Cart;

import java.util.Formatter;
import java.util.Locale;

public class CollListView extends ArrayAdapter<String> {
    public String[] names;
    private Double[] shares;
    private Activity context;

    public CollListView(Activity context, String[] name, Double[] share) {
        super(context, R.layout.activity_coll_scrolling, name);
        this.context = context;
        this.names = name;
        this.shares = share;
    }

    public CollListView(Activity context, Cart.CartUser[] users) {
        super(context, R.layout.activity_coll_scrolling, getNames(users));
        String[] userNames = new String[users.length];
        Double[] userShares = new Double[users.length];

        Cart cart = new Cart(context);
        cart.load();

        for (int i = 0; i < users.length; i++) {
            System.out.println(users[i]);

            userNames[i] = users[i].getName();
            userShares[i] = cart.getShare(users[i].getName());
        }

        this.context = context;
        this.names = userNames;
        this.shares = userShares;
    }

    private static String[] getNames(Cart.CartUser[] users) {
        String[] userNames = new String[users.length];
        for (int i = 0; i < users.length; i++) {
            userNames[i] = users[i].getName();
        }
        return userNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r == null)
        {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.activity_coll_scrolling,
                    null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) r.getTag();
        }
        System.out.println("jjjj"+names[position]);

        viewHolder.tvw1.setText(names[position]+" ha versato ");
        viewHolder.tvw2.setText(shares[position].toString());

        return r;
    }
    class  ViewHolder{
        TextView tvw1;
        TextView tvw2;

        ViewHolder(View v){
            tvw1 = v.findViewById(R.id.userName);
            tvw2 = v.findViewById(R.id.userShare);
        }
    }
}

