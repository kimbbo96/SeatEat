package com.example.myapplication.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cart implements Serializable {
    public static final long serialVersionUID = 42L;
    private transient Context context;
    private String restaurant = "";

    private List<CartFood> cartFoods = new ArrayList<>();
    private List<CartUser> cartUsers = new ArrayList<>();

    public Cart(Context context) {
        this.context = context;
    }

    public void save() {
        try {
//            String filePath = context.getFilesDir().getPath() + "/SeatEat_Cart";
//            File cartFile = new File(filePath);
//            cartFile.createNewFile();
            FileOutputStream fos = context.openFileOutput("SeatEat_Cart", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("SeatEat_Cart cannot be created or saved");
        }
    }

    public void load() {
        try {
//            String filePath = context.getFilesDir().getPath() + "/SeatEat_Cart";
//            File cartFile = new File(filePath);
//            cartFile.createNewFile();
            FileInputStream fis = context.openFileInput("SeatEat_Cart");
            ObjectInputStream is = new ObjectInputStream(fis);
            Cart oldCart = (Cart) is.readObject();
            this.restaurant = oldCart.getRestaurant();
            this.cartFoods = oldCart.getCartFoods();
            this.cartUsers = oldCart.getCartUsers();
            is.close();
            fis.close();
        } catch (FileNotFoundException | ClassCastException ex) {
            ex.printStackTrace();
            System.out.println("Creating new cart");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("SeatEat_Cart cannot be created or saved");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Cart class not found");
        }
    }

    /**
     * Clear the content of the cart, but doesn't write it on persistent storage.
     */
    public void clear() {
        restaurant = "";
        cartUsers.clear();
        cartFoods.clear();
    }

    public double getTotal() {
        return cartFoods.stream().mapToDouble(cf -> cf.getPrice() * cf.getQuantity()).sum();
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public List<CartFood> getCartFoods() {
        return cartFoods;
    }

    public void setCartFoods(List<CartFood> cartFoods) {
        this.cartFoods = cartFoods;
    }

    public void addCartFood(String id, String name, double price, String userID) {
        boolean existing = false;
        for (CartFood cf : cartFoods) {
            if (cf.id.equals(id)) {
                existing = true;
                cf.addUser(userID);
                break;
            }
        }
        if (! existing) {
            List<String> users = new ArrayList<>();
            users.add(userID);
            this.cartFoods.add(new CartFood(id, name, price, users));
        }
    }

    public void removeCartFood(String id, String userID) {
        Iterator itr = cartFoods.iterator();
        while (itr.hasNext()) {
            CartFood cf = (CartFood) itr.next();
            if (cf.id.equals(id)) {
                cf.users.remove(userID);
                if (cf.users.isEmpty()) {
                    itr.remove();
                }
            }
        }
    }

    public List<CartUser> getCartUsers() {
        return cartUsers;
    }

    public void setCartUsers(List<CartUser> cartUsers) {
        this.cartUsers = cartUsers;
    }

    public void addCartUser(String id, String name, boolean isTabletop) {
        this.cartUsers.add(new CartUser(id, name, isTabletop));
    }

    private static class CartFood implements Serializable {
        public static final long serialVersionUID = 42L;
        private String id;
        private String name;
        private double price;
        private List<String> users;

        CartFood(String id, String name, double price, List<String> users) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.users = users;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return users.size();
        }

        public List<String> getUsers() {
            return users;
        }

        public void addUser(String userID) {
            users.add(userID);
        }
    }

    private static class CartUser implements Serializable{
        public static final long serialVersionUID = 42L;
        private String id;
        private String name;
        private boolean isTabletop;

        CartUser(String id, String name, boolean isTabletop) {
            this.id = id;
            this.name = name;
            this.isTabletop = isTabletop;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean isTabletop() {
            return isTabletop;
        }
    }

}
