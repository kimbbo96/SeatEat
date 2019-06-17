package com.example.myapplication.db_obj;

public class Restaurant {
    private static final String TAG = "===Restaurant===";
    private String RESTAURANT_ID;
    private String RESTAURANT_TITLE;
    private String PRODUCT_DESCRIPTION;
    private float PRODUCT_RATING;
    private String PRODUCT_IMAGE;

    public Restaurant(String RESTAURANT_ID, String RESTAURANT_TITLE, String PRODUCT_DESCRIPTION,
                      float PRODUCT_RATING, String PRODUCT_IMAGE) {
        this.RESTAURANT_ID = RESTAURANT_ID;
        this.RESTAURANT_TITLE = RESTAURANT_TITLE;
        this.PRODUCT_DESCRIPTION = PRODUCT_DESCRIPTION;
        this.PRODUCT_RATING = PRODUCT_RATING;
        this.PRODUCT_IMAGE = PRODUCT_IMAGE;
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

    public String getPRODUCT_DESCRIPTION() {
        return PRODUCT_DESCRIPTION;
    }

    public float getPRODUCT_RATING() {
        return PRODUCT_RATING;
    }

    public String getPRODUCT_IMAGE() {
        return PRODUCT_IMAGE;
    }
}
