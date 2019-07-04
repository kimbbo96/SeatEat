package com.example.myapplication;

import android.os.Bundle;

import com.example.myapplication.utils.Cart;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

    String urlBase = "https://seateat-be.herokuapp.com";

//    private static Food f1 = new Food("1", "Supplì", "Riso, mozzarella, pomodoro...", 1, "suppli");
//    private static Food f2 = new Food("2", "Crocchetta", "Patate, mozzarella...", 2, "crocchette");
//    private static Food f3 = new Food("3", "Fiore di zucca", "Fiore di zucca, mozzarella, alici...", 3, "fiori");

//    static Food[] foods = {f1, f2, f3};
    List<Food> foods = new ArrayList<>();
    Map<String, ArrayList<String>> dishes = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_rest);

        final Restaurant rist = (Restaurant) getIntent().getSerializableExtra("Restaurant");

        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ExtendedFloatingActionButton fab = findViewById(R.id.fab_food);
        Cart cart = new Cart(this);
        cart.load();
        fab.setText("Totale: " + cart.getTotal() + "€");
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

//        DrawerLayout drawer = findViewById(R.id.drawer_layout_food);
//        NavigationView navigationView = findViewById(R.id.nav_view_food);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//
//        toggle.syncState();
//        navigationView.setNavigationItemSelectedListener(this);

        ///////////////////////////////////

        String idRest = rist.getRESTAURANT_ID();

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
        Cart cart = new Cart(this);
        cart.load();
        fab.setText("Totale: " + cart.getTotal() + "€");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_food);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.MenuRest, menu);
//        return true;
//    }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
