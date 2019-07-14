package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Coll extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com/resources/menus/";
    Cart cart;
    // path_base + "/resources/menus/" + restId + "/" + foodImage[position]))

    double myShare = 0;

    String urlBase = "https://seateat-be.herokuapp.com";

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

        cart.load();

        int people = getIntent().getIntExtra("People", 1);
        double price1 = getIntent().getDoubleExtra("Price", 1);

        TextView counterPeople = findViewById(R.id.counterPeople);
        counterPeople.setText(String.valueOf(people));

        TextView totalText = findViewById(R.id.priceText);
        System.out.println("totalText: " + totalText);
        totalText.setText(String.valueOf(price1)+"€");

        TextView counterPrice = findViewById(R.id.counterPrice);
        System.out.println("counterPrice: " + counterPrice);
        counterPrice.setText(String.valueOf(price1/people)+"€");

        TextView nameText = findViewById(R.id.myName);
        nameText.setText("Tu hai versato: ");

        /*TextView shareText = findViewById(R.id.myShare);
        shareText.setText(String.valueOf(myShare)+"€");

        ImageButton addIB = findViewById(R.id.addShare);
        addIB.setOnClickListener(view -> {
            myShare++;
            shareText.setText(String.valueOf(myShare) + "€");
        });*/

        users.clear();

        //TODO riempi lista users

        users.addAll(cart.getCartUsers());

        System.out.println("CART USERS: "+cart.getCartUsersNames());

        ProgressBar progressBarResList = findViewById(R.id.progressBarResList);

        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
        Boolean isCapotavola = preferences.getBoolean("isCapotavola",false);

        if (isCapotavola) {
            OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get
            TextView errorMsg = findViewById(R.id.errorMessage);
            String url = urlBase+"/api/example/restaurants";
            Request request = new Request.Builder().url(url).build();
            cl.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Coll.this.runOnUiThread(() -> {
                        progressBarResList.setVisibility(View.GONE);
                        errorMsg.setVisibility(View.VISIBLE);
                    });
                }


                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()){
                        System.out.println("ON RESPONSE COLL OK");
                    }
                }
            });

        }

        //costruisci CollListView
        fillList(activity, users);

    }

    static void fillList(Activity activity, List<Cart.CartUser> users) {
        ListView listView = activity.findViewById(R.id.list_view_coll);
        System.out.println(listView);

        SharedPreferences preferencesLogin = activity.getSharedPreferences("loginref", MODE_PRIVATE);
        String userName = preferencesLogin.getString("nome", "");

        for (Cart.CartUser c : users) {
            if (c.getName().equals(userName)) {
                users.remove(c);
                break;
            }
        }

        Cart.CartUser[] cartUsers = users.toArray(new Cart.CartUser[0]);

        CollListView customListView = new CollListView(activity, cartUsers);

        listView.setAdapter(customListView);

        Utils.justifyListViewHeight(listView);
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