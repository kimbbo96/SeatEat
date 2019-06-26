package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.db_obj.Food;
import com.example.myapplication.db_obj.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

//    String[] foodNames = {f1.getFOOD_TITLE(),f2.getFOOD_TITLE(),f3.getFOOD_TITLE()};
//    String[] foodDes = {f1.getFOOD_SHORT_DESCR(),f2.getFOOD_SHORT_DESCR(),f3.getFOOD_SHORT_DESCR()};
//    Float[] prices = {f1.getFOOD_PRICE(),f2.getFOOD_PRICE(),f3.getFOOD_PRICE()};

    ListView listView;
    List list = new ArrayList();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_rest);

        Activity activity = this;
//        foods.clear();
        final Restaurant rist = (Restaurant) getIntent().getSerializableExtra("Restaurant");

        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab_food);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        OkHttpClient cl = new OkHttpClient(); // inizio la procedura di get
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
                    FoodRest.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(muresponse);
                            try {
                                JSONArray jsonArray = new JSONArray(muresponse);
                                for (int i = 0 ; i< jsonArray.length(); i++){ // for each food
                                    JSONObject jsonFood = jsonArray.getJSONObject(i);
                                    String id = jsonFood.getString("id");
                                    String name = jsonFood.getString("nome");
                                    String shordDescr = jsonFood.getString("descrBreve");
                                    String longDescr = jsonFood.getString("descrLunga");
                                    String dish = jsonFood.getString("portata");
                                    double price = jsonFood.getDouble("costo");
                                    String image = jsonFood.getString("immagine");
                                    System.out.println("wasd"+jsonFood);

                                    Food food = new Food(id, name, shordDescr, longDescr, dish, price, image);
                                    foods.add(food);
                                }
                                System.out.println("okoko food" + foods.size());

                                listView = findViewById(R.id.list_view_food);
                                System.out.println(listView);

                                FoodListView customListView = new FoodListView(activity, foods.toArray(new Food[0]), rist.getRESTAURANT_ID());

                                System.out.println("foooooooodd" + customListView + listView);
                                listView.setAdapter(customListView);

                                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                                    System.out.println("hai clikkato "+i);

                                    /*Intent intent = new Intent(FoodRest.this,ResDetail.class);
                                    intent.putExtra("Restaurant",resList.get(i)); // passo l'oggetto ristornate
                                    startActivity(intent);*/
                                });

                            }catch (JSONException err){
                                Log.d("Error", err.toString());
                            }
                        }
                    });
                }
            }
        });

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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
