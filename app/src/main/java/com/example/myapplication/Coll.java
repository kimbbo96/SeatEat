package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


public class Coll extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com/resources/menus/";
    Cart cart;
    // path_base + "/resources/menus/" + restId + "/" + foodImage[position]))

    static List<Cart.CartUser> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cart = new Cart(this);

        setContentView(R.layout.activity_coll);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO manage cart
        Activity activity = this;


        int people = getIntent().getIntExtra("People", 1);

        TextView counterPeople = findViewById(R.id.counterPeople);
        counterPeople.setText(String.valueOf(people));

        int price = getIntent().getIntExtra("Price", 1);

        TextView totalText = findViewById(R.id.priceText);
        totalText.setText(String.valueOf(price)+"€");

        TextView counterPrice = findViewById(R.id.counterPrice);
        counterPrice.setText(String.valueOf(price/people)+"€");

        users.clear();

        //TODO riempi lista users

        users.addAll(cart.getCartUsers());

        //costruisci CollListView
        fillList(activity, users);

    }

    static void fillList(Activity activity, List<Cart.CartUser> users) {
        ListView listView = activity.findViewById(R.id.list_view_coll);
        System.out.println(listView);

        CollListView customListView = new CollListView(activity, users.toArray(new Cart.CartUser[0]));

        System.out.println("aaaaaaaaaaas"+customListView+ listView);
        listView.setAdapter(customListView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}