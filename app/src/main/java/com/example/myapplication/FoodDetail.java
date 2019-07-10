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


public class FoodDetail extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com/resources/menus/";
    Cart cart = new Cart(this);
    // path_base + "/resources/menus/" + restId + "/" + foodImage[position]))

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Food food = (Food) getIntent().getSerializableExtra("Food");
        String rid = getIntent().getStringExtra("RestID");
        final String restID = rid == null ? "1" : rid;
        String n = getIntent().getStringExtra("Note");
        final String note = n == null ? "" : n;
        final String mode = getIntent().getStringExtra("Mode");
        final int quantity = getIntent().getIntExtra("Quantity", 1);
        System.out.println("EXTRA quantity: " + quantity);

        TextView foodName = findViewById(R.id.foodNameDetail);
        foodName.setText(food.getFOOD_TITLE());

        TextView foodDesc = findViewById(R.id.foodDesDetail);
        foodDesc.setText(food.getFOOD_SHORT_DESCR());

        TextView foodDetail = findViewById(R.id.foodDetail);
        foodDetail.setText(food.getFOOD_LONG_DESCR());

        TextView foodPriceDetail = findViewById(R.id.foodPriceDetail);
        foodPriceDetail.setText(food.getFOOD_PRICE() + "€");

        EditText notesField = findViewById(R.id.foodNotesDetail);
        notesField.setText(note);

        Activity activity = this;
        String imgName = food.getFOOD_IMAGE();
        ImageView copertina = findViewById(R.id.copertinaFoodDetail);
        String imgURL = path_base + restID + "/" + imgName;
        Glide.with(activity).load(Uri.parse(imgURL)).into(new CustomViewTarget<ImageView, Drawable>(copertina) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                copertina.setBackground(resource);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Glide.with(activity).load(R.drawable.no_internet).into(copertina);
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {}
        });

        // TODO manage cart
        SharedPreferences preferences = activity.getSharedPreferences("loginref", MODE_PRIVATE);
        String userId = preferences.getString("nome", "");
        int[] counter = {quantity};
        TextView counterDetail = findViewById(R.id.counterDetail);
        counterDetail.setText(String.valueOf(quantity));

        // add to (sub)cart
        ImageButton addIB = findViewById(R.id.addFoodButtonDetail);
        addIB.setOnClickListener(view -> {
            counter[0] = counter[0] + 1;
            counterDetail.setText(Integer.toString(counter[0]));
            // vh.cartButton.setText("Totale: " + cart.getTotal() + "€");
        });

        // remove from (sub)cart
        ImageButton remIB = findViewById(R.id.removeFoodButtonDetail);
        remIB.setOnClickListener(view -> {
            if (counter[0] > 0) {
                counter[0] = counter[0] - 1;
                counterDetail.setText(Integer.toString(counter[0]));
//            vh.cartButton.setText("Totale: " + cart.getTotal() + "€");
            }
        });

        Button button = findViewById(R.id.aggiungiButtonDetail);
        button.setOnClickListener(v -> {
            String notes = notesField.getText().toString();
            System.out.println("hai clikkato AGGIUNGI " + food.getFOOD_TITLE() + "(id " + food.getFOOD_ID() + ")\nNotes: '" + notes + "'");

            cart.load();
            if (mode.equals("edit")) {
                for (int i = 0; i < counter[0]; i++) {
                    cart.removeCartFood(food.getFOOD_ID(), userId, note);
                }
            }
            for (int i = 0; i < counter[0]; i++) {
                cart.addCartFood(food.getFOOD_ID(), food.getFOOD_TITLE(), food.getFOOD_PRICE(),
                        userId, notes, food.getFOOD_SHORT_DESCR(), food.getFOOD_LONG_DESCR(), food.getFOOD_IMAGE());
            }
            cart.save();

            finish();

//            Intent intent = new Intent(ResDetail.this,FoodRest.class);
//            intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
//            startActivity(intent);
        });
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
