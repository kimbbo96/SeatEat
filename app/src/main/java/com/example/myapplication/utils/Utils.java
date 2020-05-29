package com.example.myapplication.utils;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.signature.ObjectKey;
import com.example.myapplication.Help;
import com.example.myapplication.MenuRest;
import com.example.myapplication.R;
import com.example.myapplication.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Utils {

    /**
     * Measures the distance between two points.
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @param unit the desired unit of measure ('M' for miles or 'K' for kilometers)
     * @return the distance between the two points
     *
     * @see "https://stackoverflow.com/a/3694410"
     */
    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        }
        return (dist);
    }

    public static void justifyListViewHeight (ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "canale per le notifiche";
            String id = "1";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            System.out.println("creato canale " + id);
        }
    }

    public static void main(String[] args) {
        double d = distance(41.968043, 12.537057, 41.927775, 12.480815, 'K');
        System.out.println(d);

        double[] portaDiRoma = {41.971655, 12.540330};

        Map<String, List<String>> dishes = new HashMap<>();
        List<String> foods = new ArrayList<>(Arrays.asList("gnocchi", "pasta", "riso"));
        dishes.put("primo", foods);
        List<String> secondi = dishes.getOrDefault("secondo", new ArrayList<>());
        secondi.add("bistecca");
        dishes.put("secondo", secondi);
        dishes.computeIfAbsent("contorno", k -> new ArrayList<>()).add("pomodori");
        System.out.println(dishes);
    }


    public static void gestisciMenu (MenuItem item, Context context, DrawerLayout drawerLayout){
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String contextName = context.getClass().getName().split("\\.")
                [context.getClass().toString().split("\\.").length-1];

        if (id == R.id.nav_home && !contextName.equals("MenuRest")) {
            Intent intent = new Intent(context, MenuRest.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }  else if (id == R.id.nav_help && !contextName.equals("Help")) {
            Intent intent = new Intent(context, Help.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } else if (id == R.id.nav_share && !contextName.equals("Share")) {

        } else if (id == R.id.nav_send && !contextName.equals("Send")) {

        }

        else if (id == R.id.nav_settings && !contextName.equals("Settings")){


            Intent intent = new Intent(context, Settings.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
        drawerLayout.closeDrawer(GravityCompat.START);


    }

    public static void clearResPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("infoRes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("QRimage",null);
        editor.putBoolean("isCapotavola",false);
        editor.putString("ID", "");
        editor.commit();
    }

    public static void showDialog(Context context, String title, CharSequence message,
                                  String posButtonText, DialogInterface.OnClickListener posButtonListener,
                                  String negButtonText, DialogInterface.OnClickListener negButtonListener) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message);

        // add the buttons
        builder.setPositiveButton(posButtonText, posButtonListener);
        builder.setNegativeButton(negButtonText, negButtonListener);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
