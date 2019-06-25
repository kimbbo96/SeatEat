package com.example.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.db_obj.Food;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodRest extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Food f1 = new Food("1", "Supplì", "Riso, mozzarella, pomodoro...", 1, "suppli");
    private static Food f2 = new Food("2", "Crocchetta", "Patate, mozzarella...", 2, "crocchette");
    private static Food f3 = new Food("3", "Fiore di zucca", "Fiore di zucca, mozzarella, alici...", 3, "fiori");

    static Food[] foods = {f1, f2, f3};

//    String[] foodNames = {f1.getFOOD_TITLE(),f2.getFOOD_TITLE(),f3.getFOOD_TITLE()};
//    String[] foodDes = {f1.getFOOD_DESCRIPTION(),f2.getFOOD_DESCRIPTION(),f3.getFOOD_DESCRIPTION()};
//    Float[] prices = {f1.getFOOD_PRICE(),f2.getFOOD_PRICE(),f3.getFOOD_PRICE()};

    ListView listView;
    List list = new ArrayList();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_rest);

        Toolbar toolbar = findViewById(R.id.tool_bar_simple);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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

        listView = findViewById(R.id.list_view_food);
        System.out.println(listView);

//        FoodListView customListView = new FoodListView(this,foodNames,foodDes,prices);
        FoodListView customListView = new FoodListView(this, foods);

        System.out.println("foooooooodd"+customListView+ listView);
        listView.setAdapter(customListView);

//        final List<Food> foodList = new ArrayList<Food>(Arrays.asList(foods));
//        foodList.add(f1); // prova
//        foodList.add(f2);
//        foodList.add(f3);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            System.out.println("hai clikkato "+i);

            /*Intent intent = new Intent(FoodRest.this,ResDetail.class);
            intent.putExtra("Restaurant",resList.get(i)); // passo l'oggetto ristornate
            startActivity(intent);*/
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
