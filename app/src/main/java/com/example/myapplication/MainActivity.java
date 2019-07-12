package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.myapplication.utils.Utils;


public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    private static int TIME_OUT = 500;
    private static double[] PDR_POS = {41.971655, 12.540330};
    private static double[] SAPI_POS = {41.899189, 12.517717};
    private static double[] DEFAULT_POS = PDR_POS;
    public static Location here = new Location("");
    static {
        // If the first registered position isn't the real one, we can approximate with a likely one
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
        } finally {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("true jjjj"+ preferences.getString("nome",null));

        Utils.createNotificationChannel(this);
        Bundle b = getIntent().getExtras();
        System.out.println("ONCREATE MAIN EXTRAS: " + b);
        if (b!=null) {
            String type = b.getString("type");
            String id = b.getString("id");
            String capotavola = b.getString("isCapotavola");
            System.out.println("ONCREATE MAIN type=" + type + " id=" + id + ", capotavola=" + capotavola);
        }

        // Get location or location permission
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // Show logo
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, MenuRest.class);
                    startActivity(i);
                    finish();
                }
            }, TIME_OUT);

        } catch (SecurityException se) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // Handle login
        preferences = getSharedPreferences("loginref", MODE_PRIVATE);
        boolean savelogin = preferences.getBoolean("savelogin", false);
        System.out.println("ghjkl");
        if (savelogin) {
            System.out.println("true valore"+ preferences.getString("nome",null));
            Toast.makeText(this, "ciao " + preferences.getString("nome", null), Toast.LENGTH_LONG).show();
            System.out.println(preferences.getString("nome", null));

            setContentView(R.layout.activity_main);
        }
        else {
            // Session management
            Toast.makeText(this,"prima volta", Toast.LENGTH_LONG).show();
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();

        }
    }

    @Override
    public void onResume(){
        super.onResume();

        Bundle b = getIntent().getExtras();
        System.out.println("ONRESUME MAIN EXTRAS=" + b);

        if (b!=null) {
            String type = b.getString("type");
            String id = b.getString("id");
            String capotavola = b.getString("isCapotavola");
            System.out.println("ONRESUME MAIN type=" + type + " id=" + id + ", capotavola=" + capotavola);
        }
    }
}
