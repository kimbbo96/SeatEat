package com.example.myapplication.db_obj;

import java.io.Serializable;

public class Food implements Serializable {
    private static final String TAG = "===Food===";
    private String FOOD_ID;
    private String FOOD_TITLE;
    private String FOOD_DESCRIPTION;
    private float FOOD_PRICE;

    public Food(String FOOD_ID, String FOOD_TITLE, String FOOD_DESCRIPTION,
                      float FOOD_PRICE) {
        this.FOOD_ID = FOOD_ID;
        this.FOOD_TITLE = FOOD_TITLE;
        this.FOOD_DESCRIPTION = FOOD_DESCRIPTION;
        this.FOOD_PRICE = FOOD_PRICE;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getFOOD_ID() {
        return FOOD_ID;
    }

    public String getFOOD_TITLE() {
        return FOOD_TITLE;
    }

    public String getFOOD_DESCRIPTION() {
        return FOOD_DESCRIPTION;
    }

    public float getFOOD_PRICE() {
        return FOOD_PRICE;
    }
}

