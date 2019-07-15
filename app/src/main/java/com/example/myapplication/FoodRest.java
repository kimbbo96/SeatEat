package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.utils.Cart;
import com.example.myapplication.utils.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FoodRest extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences preferences;
    String urlBase = "https://seateat-be.herokuapp.com";
    Cart cart;

//    private static Food f1 = new Food("1", "Supplì", "Riso, mozzarella, pomodoro...", 1, "suppli");
//    private static Food f2 = new Food("2", "Crocchetta", "Patate, mozzarella...", 2, "crocchette");
//    private static Food f3 = new Food("3", "Fiore di zucca", "Fiore di zucca, mozzarella, alici...", 3, "fiori");

//    static Food[] foods = {f1, f2, f3};
    List<Food> foods = new ArrayList<>();
    Map<String, ArrayList<String>> dishes = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cart = new Cart(this);
        setContentView(R.layout.activity_food_rest);

        SharedPreferences preferencesLogin = this.getSharedPreferences("loginref", MODE_PRIVATE);
        String userId = preferencesLogin.getString("nome", "");
        SharedPreferences preferencesRest = this.getSharedPreferences("infoRes", MODE_PRIVATE);
        String idRestPref = preferencesRest.getString("ID","");
        final Restaurant rist = (Restaurant) getIntent().getSerializableExtra("Restaurant");
        String idRest = rist == null ? idRestPref : rist.getRESTAURANT_ID();

        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab_food);
        if (idRest.equals(idRestPref)) {
            cart.load();
            fab.setText("Totale: " + cart.getTotal() + "€");
            fab.setOnClickListener(view -> {
//                cart.load();        // TODO solo per DEBUG! togliere!!!
//                cart.fakeCart();   // TODO solo per DEBUG! togliere!!!
//                cart.save();        // TODO solo per DEBUG! togliere!!!

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
                System.out.println("hai clikkato il carrello");
                Intent intent = new Intent(this, CartActivity.class);
                intent.putExtra("RestId", idRest); // passo l'oggetto ristornate
                startActivity(intent);
            });
        } else {
            fab.setVisibility(View.GONE);
        }


        NavigationView navigationView = findViewById(R.id.nav_view);
//

        navigationView.setNavigationItemSelectedListener(this);

        ///////////////////////////////////

        // inizio la procedura di get
        OkHttpClient cl = new OkHttpClient();
        String url = urlBase + "/api/menus/" + idRest;
        Request request = new Request.Builder().url(url).build();
        cl.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("food download error");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String muresponse = response.body().string();
                    FoodRest.this.runOnUiThread(() -> {
                        System.out.println(muresponse);
                        try {
                            JSONArray jsonArray = new JSONArray(muresponse);
                            for (int i = 0 ; i< jsonArray.length(); i++){ // for each food
                                JSONObject jsonFood = jsonArray.getJSONObject(i);
                                String dish = jsonFood.getString("portata");

                                String serializedFood = jsonFood.toString();
                                dishes.computeIfAbsent(dish, k -> new ArrayList<>()).add(serializedFood);
                            }
                            System.out.println("okoko food" + foods.size());

                            // passa la portata e la relativa sottolista (cibi di quella portata)
                            CollectionFoodFragment cdf = new CollectionFoodFragment();
                            Bundle param = new Bundle();
                            param.putString("restID", idRest);
                            param.putString("userID", userId);
                            param.putStringArrayList("dishes", new ArrayList<>(dishes.keySet()));
                            for (Map.Entry<String, ArrayList<String>> entry : dishes.entrySet()) {
                                param.putStringArrayList(entry.getKey(), entry.getValue());
                            }
                            cdf.setArguments(param);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(R.id.head_container, cdf)
                                    .commit();

                        } catch (JSONException err){
                            Log.d("Error", err.toString());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_food);
        cart.load();
        System.out.println("CARRELLO ONRESUME: " + cart);
        fab.setText("Totale: " + cart.getTotal() + "€");
    }

    @Override
    public void onBackPressed() {
        System.out.println("akallaalala");
        DrawerLayout drawer = findViewById(R.id.drawer_layout_food);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        TextView name_tab = findViewById(R.id.usr_name_tab);
        name_tab.setText( preferences.getString("nome", null));

        ImageView profile_image = findViewById(R.id.profile_image);
        System.out.println(urlBase+preferences.getString("immagine",null));
        RequestBuilder<Drawable> error = Glide.with(this).load(R.drawable.no_internet);
        Glide.with(this).load(urlBase+"/"+preferences.getString("immagine",null)).error(error)
                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                .fitCenter().into(profile_image);


        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("aaaaaa");
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
        } else if (id == R.id.qr_button) {
            SharedPreferences preferences = getSharedPreferences("infoRes", MODE_PRIVATE);
            String QREncoded = preferences.getString("QRimage",null);
            byte[] decodedByte = Base64.getDecoder().decode(QREncoded);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

            Intent zoomQRIntent = new Intent(getApplicationContext(), Qr_zoom.class);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            zoomQRIntent.putExtra("QRImage", b);
            startActivity(zoomQRIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Utils.gestisciMenu(item,this,findViewById(R.id.drawer_layout_food));
        return true;
    }
}
