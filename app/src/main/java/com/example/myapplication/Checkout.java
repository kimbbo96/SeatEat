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
import com.google.android.material.snackbar.Snackbar;


public class Checkout extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com/resources/menus/";
    Cart cart;
    // path_base + "/resources/menus/" + restId + "/" + foodImage[position]))

    private int people = 0;
    private double price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cart = new Cart(this);

        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO manage cart
        Activity activity = this;

        cart.load();

        people = cart.getCartUsers().size();

        int[] counter1 = {people};

        TextView counterPeople = findViewById(R.id.counterPeopleCheck);
        counterPeople.setText(String.valueOf(people));

        price = cart.getTotal();

        TextView totalText = findViewById(R.id.priceTextCheck);
        System.out.println("totalText: " + totalText);
        totalText.setText(String.valueOf(price)+"€");

        TextView counterPrice = findViewById(R.id.counterPriceCheck);
        System.out.println("counterPrice: " + counterPrice);
        counterPrice.setText(String.valueOf(price/people)+"€");

        // add people
        ImageButton addIB = findViewById(R.id.addPeopleCheck);
        addIB.setOnClickListener(view -> {
            counter1[0] = counter1[0] + 1;
            counterPeople.setText(Integer.toString(counter1[0]));
            counterPrice.setText(price/counter1[0] + "€");
        });

        // remove from (sub)cart
        ImageButton remIB = findViewById(R.id.removePeopleCheck);
        remIB.setOnClickListener(view -> {
            if (counter1[0] > 0) {
                counter1[0] = counter1[0] - 1;
                counterPeople.setText(Integer.toString(counter1[0]));
                counterPrice.setText(price/counter1[0] + "€");
            }
        });
    }

    public void payWithCash(View view) {
        System.out.println("hai clikkato CASH");
        /*Intent intent = new Intent(this, Help.class);
        startActivity(intent);*/
    }

    public void payWithCard(View view) {
        System.out.println("hai clikkato CARD");
        /*Intent intent = new Intent(this, Help.class);
        startActivity(intent);*/
    }

    public void payWithColl(View view) {
        System.out.println("hai clikkato COLL");
        Intent intent = new Intent(this, Coll.class);

        intent.putExtra("People", people); // passo l'oggetto ristorante
        intent.putExtra("Price", price); // passo l'oggetto ristorante

        startActivity(intent);
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