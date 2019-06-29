package com.example.myapplication.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

/**
 * @see "https://stackoverflow.com/a/3145655"
 */
public class MyLocation {
    private Timer timer1;
    private LocationManager lm;
    private Context context;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private Location myLocation = null;
    private int delay = 20000;

    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            myLocation = location;
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            myLocation = location;
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    public MyLocation(Context context) {
        this.context = context;

        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    gps_enabled = true;
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                } else if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    network_enabled = true;
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
                }
            }
        } catch(Exception ex) {
            System.out.println("error with location permissions");
            ex.printStackTrace();
        }

        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), delay);
    }

    public Location getMyLocation() {
        return myLocation;
    }

    class GetLastLocation extends TimerTask {

        @Override
        public void run() {
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if(gps_enabled) {
                try {
                    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException ex) {
                    System.out.println("error with location permissions");
                    ex.printStackTrace();
                    ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
            if(network_enabled) {
                try {
                    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException ex) {
                    System.out.println("error with location permissions");
                    ex.printStackTrace();
                    ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }

            //if there are both values use the latest one
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    myLocation = gps_loc;
                else
                    myLocation = net_loc;
                return;
            }

            if(gps_loc!=null){
                myLocation = gps_loc;
                return;
            }

            if(net_loc!=null){
                myLocation = net_loc;
                return;
            }
        }
    }
}