package com.example.myapplication.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}