package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.myapplication.db_obj.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class scrolling_restaurant extends AppCompatActivity {
    // prove
    Restaurant r1 = new Restaurant("1","IV Secolo",
            "trattoria","5","image");
    Restaurant r2 = new Restaurant("2","Panizzeri",
            "panineria","3","image");
    Restaurant r3 = new Restaurant("3","jinja",
            "jappo","1","image");

    String[] resNames = {r1.getRESTAURANT_TITLE(),r2.getRESTAURANT_TITLE(),r3.getRESTAURANT_TITLE()};
    String[] resDes = {r1.getPRODUCT_DESCRIPTION(),r2.getPRODUCT_DESCRIPTION(),r3.getPRODUCT_DESCRIPTION()};
    Integer[] imgID = {R.drawable.iv_secolo_logo,R.drawable.panizzeri_logo,R.drawable.jinja_logo};
    ListView listView;
    List list = new ArrayList();
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        listView= (ListView) findViewById(R.id.list_view);
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        CustomListView customListView = new CustomListView(this,resNames,resDes,imgID);

        System.out.println("aaaaaaaaaaas"+customListView+ listView);
        listView.setAdapter(customListView);
        /*
        listView = findViewById(R.id.list_view);

        list.add(r1.getRESTAURANT_TITLE());
        list.add(r2.getRESTAURANT_TITLE());
        list.add(r3.getRESTAURANT_TITLE());
        adapter = new ArrayAdapter(scrolling_restaurant.this,
                android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);*/
    }
}
