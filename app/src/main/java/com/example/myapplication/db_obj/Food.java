package com.example.myapplication.db_obj;

import java.io.Serializable;

public class Food implements Serializable {
    private static final String TAG = "===Food===";
    private String FOOD_ID;
    private String FOOD_TITLE;
    private String FOOD_SHORT_DESCR;
    private String FOOD_LONG_DESCR;
    private double FOOD_PRICE;
    private String FOOD_IMAGE;

    public Food(String FOOD_ID, String FOOD_TITLE, String SHORT_DESCR, String LONG_DESCR,
                      double FOOD_PRICE, String FOOD_IMAGE) {
        this.FOOD_ID = FOOD_ID;
        this.FOOD_TITLE = FOOD_TITLE;
        this.FOOD_SHORT_DESCR = SHORT_DESCR;
        this.FOOD_LONG_DESCR = LONG_DESCR;
        this.FOOD_PRICE = FOOD_PRICE;
        this.FOOD_IMAGE = FOOD_IMAGE;
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

    public String getFOOD_SHORT_DESCR() {
        return FOOD_SHORT_DESCR;
    }

    public String getFOOD_LONG_DESCR() {
        return FOOD_LONG_DESCR;
    }

    public double getFOOD_PRICE() {
        return FOOD_PRICE;
    }

    public String getFOOD_IMAGE() {
        return FOOD_IMAGE;
    }

    @Override
    public String toString() {
        return "Food{" +
                "FOOD_ID='" + FOOD_ID + "',\n" +
                "FOOD_TITLE='" + FOOD_TITLE + "',\n" +
                "FOOD_SHORT_DESCR='" + FOOD_SHORT_DESCR + "',\n" +
                "FOOD_PRICE=" + FOOD_PRICE + "',\n" +
                "FOOD_IMAGE='" + FOOD_IMAGE + '}';
    }
}

