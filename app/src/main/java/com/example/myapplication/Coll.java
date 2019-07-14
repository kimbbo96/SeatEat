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
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class Coll extends AppCompatActivity {
    private String path_base = "https://seateat-be.herokuapp.com/resources/menus/";
    Cart cart;
    // path_base + "/resources/menus/" + restId + "/" + foodImage[position]))

    double myShare = 0;

    double totalShares = 0;

    String urlBase = "https://seateat-be.herokuapp.com";

    private final String POST_URL = "https://seateat-be.herokuapp.com/api/pushcart";    // post autenticazione nell'header con cart nel body

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
        double price = getIntent().getDoubleExtra("Price", 1);

        TextView counterPeople = findViewById(R.id.counterPeople);
        counterPeople.setText(String.valueOf(people));

        TextView totalText = findViewById(R.id.priceText);
        System.out.println("totalText: " + totalText);
        totalText.setText(String.valueOf(price)+"€");

        TextView counterPrice = findViewById(R.id.counterPrice);
        System.out.println("counterPrice: " + counterPrice);
        counterPrice.setText(String.valueOf(price/people)+"€");

        TextView nameText = findViewById(R.id.myName);
        nameText.setText("Quanto vuoi versare?");

        EditText myShareEditText = findViewById(R.id.shareEditText);

        users.clear();

        //TODO riempi lista users

        users.addAll(cart.getCartUsers());

        System.out.println("CART USERS: "+cart.getCartUsersNames());

        ProgressBar progressBarResList = findViewById(R.id.progressBarResList);

        SharedPreferences preferences = activity.getSharedPreferences("infoRes", MODE_PRIVATE);
        Boolean isCapotavola = preferences.getBoolean("isCapotavola",false);

        if (isCapotavola) {

            new Thread(()->{

                OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get
                TextView errorMsg = findViewById(R.id.errorMessage);
                String url = urlBase+"/api/colletta";
                Request request = new Request.Builder().url(url).build();

                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                String token = preferences.getString("nome", null) + ":" + preferences.getString("password", null);
                String credenziali = preferences.getString("nome", null) + ":" + preferences.getString("password", null);
                String BasicBase64format = "Basic " + Base64.getEncoder().encodeToString(credenziali.getBytes());

                JSONObject colletta = new JSONObject();
                try {
                    colletta.put("quantita", 10);
                } catch (JSONException e) {
                    Log.d("OKHTTP3", "JSON exception");
                    e.printStackTrace();
                }

                RequestBody bodyUp = RequestBody.create(JSON, colletta.toString());

                Request uploadReq = new Request.Builder()
                        .url(POST_URL)
                        .post(bodyUp)
                        .addHeader("Authorization", BasicBase64format)
                        .build();
                try {
                    Response response = cl.newCall(uploadReq).execute();

                    if (response.isSuccessful()) {
                        System.out.println("CART REQUEST post SUCCESSFUL" + response.message());
                        System.out.println("CART REQUEST post SUCCESSFUL" + response.isSuccessful());
                    } else {
                        System.out.println("CART REQUEST post UNSUCCESSFUL" + response.message());
                        System.out.println("CART REQUEST post UNSUCCESSFUL" + response.isSuccessful());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).start();


        }

        for (Cart.CartUser u : users) {
            u.setShare(10.0);

            totalShares += u.getShare();
        }

        TextView totalObtained = findViewById(R.id.counterTotal);
        totalObtained.setText(totalShares+"€ su "+price+"€");

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hai clikkato ANNULLA");
                /*Intent intent = new Intent(ResDetail.this,FoodRest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);*/
            }
        });


        Button payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("hai clikkato PAGA");
                /*Intent intent = new Intent(ResDetail.this,FoodRest.class);
                intent.putExtra("Restaurant",rist); // passo l'oggetto ristornate
                startActivity(intent);*/
            }
        });


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