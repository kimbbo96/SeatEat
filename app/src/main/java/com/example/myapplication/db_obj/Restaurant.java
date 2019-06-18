package com.example.myapplication.db_obj;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private static final String TAG = "===Restaurant===";
    private String RESTAURANT_ID;
    private String RESTAURANT_TITLE;
    private String RESTAURANT_DESCRIPTION;
    private float RESTAURANT_RATING;
    private String RESTAURANT_IMAGE;

    public Restaurant(String RESTAURANT_ID, String RESTAURANT_TITLE, String RESTAURANT_DESCRIPTION,
                      float PRODUCT_RATING, String PRODUCT_IMAGE) {
        this.RESTAURANT_ID = RESTAURANT_ID;
        this.RESTAURANT_TITLE = RESTAURANT_TITLE;
        this.RESTAURANT_DESCRIPTION = RESTAURANT_DESCRIPTION;
        this.RESTAURANT_RATING = PRODUCT_RATING;
        this.RESTAURANT_IMAGE = PRODUCT_IMAGE;
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

    public String getRESTAURANT_DESCRIPTION() {
        return RESTAURANT_DESCRIPTION;
    }

    public float getPRODUCT_RATING() {
        return RESTAURANT_RATING;
    }

    public String getPRODUCT_IMAGE() {
        return RESTAURANT_IMAGE;
    }
}
