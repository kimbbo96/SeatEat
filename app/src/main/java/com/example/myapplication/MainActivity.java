package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.utils.UserSession;


public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static int TIME_OUT = 1500;
    private static double[] PDR_POS = {41.971655, 12.540330};
    private static double[] SAPI_POS = {41.899189, 12.517717};
    private static double[] DEFAULT_POS = PDR_POS;
    public static Location here = new Location("");
    static {
        here.setLatitude(DEFAULT_POS[0]);
        here.setLongitude(DEFAULT_POS[1]);
    }

    private LocationManager lm;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            here = location;
//            System.out.println(location);
//            Log.i("===Main===", "position: " + location.getLatitude() + ", " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get location or location permission
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException se) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Handle login
        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        boolean savelogin = preferences.getBoolean("savelogin", false);
        System.out.println("ghjkl");
        if (savelogin) {
            System.out.println("true valore");
            Toast.makeText(this, "ciao" + preferences.getString("nome", null), Toast.LENGTH_LONG).show();
            System.out.println(preferences.getString("nome", null));

            setContentView(R.layout.activity_main);

            // Show logo
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, MenuRest.class);
                    startActivity(i);
                    finish();
                }
            }, TIME_OUT);
        }
        else {
            // Session management
            Toast.makeText(this,"prima volta", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
        }
    }
}
