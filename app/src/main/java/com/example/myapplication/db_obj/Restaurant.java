package com.example.myapplication.db_obj;

import java.io.Serializable;
import java.util.Arrays;

public class Restaurant implements Serializable {
    private static final String TAG = "===Restaurant===";
    private String RESTAURANT_ID;
    private String RESTAURANT_TITLE;
    private String RESTAURANT_TYPOLOGY;
    private float RESTAURANT_RATING;
    private String RESTAURANT_IMAGE;
    private Double[] RESTAURANT_POSITION;

    public Restaurant(String RESTAURANT_ID, String RESTAURANT_TITLE, String RESTAURANT_TYPOLOGY,
                      float RESTURANT_RATING, String RESTURANT_IMAGE, Double[] RESTAURANT_POSITION) {
        this.RESTAURANT_ID = RESTAURANT_ID;
        this.RESTAURANT_TITLE = RESTAURANT_TITLE;
        this.RESTAURANT_TYPOLOGY = RESTAURANT_TYPOLOGY;
        this.RESTAURANT_RATING = RESTURANT_RATING;
        this.RESTAURANT_IMAGE = RESTURANT_IMAGE;
        this.RESTAURANT_POSITION = RESTAURANT_POSITION;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getRESTAURANT_ID() {
        return RESTAURANT_ID;
    }

    public String getRESTAURANT_TITLE() {
        return RESTAURANT_TITLE;
    }

    public String getRESTAURANT_TYPOLOGY() {
        return RESTAURANT_TYPOLOGY;
    }

    public float getRESTAURANT_RATING() {
        return RESTAURANT_RATING;
    }

    public String getRESTAURANT_IMAGE() {
        return RESTAURANT_IMAGE;
    }

    public Double[] getRESTAURANT_POSITION() {
        return RESTAURANT_POSITION;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "RESTAURANT_ID='" + RESTAURANT_ID + "',\n" +
                "RESTAURANT_TITLE='" + RESTAURANT_TITLE + "',\n" +
                "RESTAURANT_TYPOLOGY='" + RESTAURANT_TYPOLOGY + "',\n" +
                "RESTAURANT_RATING='" + RESTAURANT_RATING + "',\n" +
                "RESTAURANT_IMAGE='" + RESTAURANT_IMAGE + "',\n" +
                "RESTAURANT_POSITION='" + Arrays.toString(RESTAURANT_POSITION) + "'}";
    }
}
