package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.db_obj.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class menu_rest extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Restaurant r1 = new Restaurant("1","Trattoria IV Secolo",
            "Trattoria, Italiano",(float)6,"image", new Double[] {42.002780, 12.384011});
    Restaurant r2 = new Restaurant("2","Panizzeri",
            "Panineria, Italiano",(float)4,"image", new Double[] {41.968043, 12.537057});
    Restaurant r3 = new Restaurant("3","Ristorante Jinja",
            "Giapponese, Cinese",(float)1,"image", new Double[] {41.927775, 12.480815});


    String[] resNames = {r1.getRESTAURANT_TITLE(),r2.getRESTAURANT_TITLE(),r3.getRESTAURANT_TITLE()};
    String[] resDes = {r1.getRESTAURANT_TYPOLOGY(),r2.getRESTAURANT_TYPOLOGY(),r3.getRESTAURANT_TYPOLOGY()};
    Integer[] imgID = {R.drawable.iv_secolo_logo,R.drawable.panizzeri_logo,R.drawable.jinja_logo};
    Float[] rate = {r1.getPRODUCT_RATING(),r2.getPRODUCT_RATING(),r3.getPRODUCT_RATING()};

    ListView listView;
    List list = new ArrayList();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_rest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        ///////////////////////////////////

        listView= (ListView) findViewById(R.id.list_view1);
        System.out.println(listView);

        RestListView customListView = new RestListView(this,resNames,resDes,imgID,rate);

        System.out.println("aaaaaaaaaaas"+customListView+ listView);
        listView.setAdapter(customListView);
        final List<Restaurant> resList = new ArrayList<Restaurant>();
        resList.add(r1); // prova
        resList.add(r2);
        resList.add(r3);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("hai clikkato "+i);
                Intent intent = new Intent(menu_rest.this,res_detail.class);
                intent.putExtra("Restaurant",resList.get(i)); // passo l'oggetto ristornate
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rest, menu);
        return true;
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
