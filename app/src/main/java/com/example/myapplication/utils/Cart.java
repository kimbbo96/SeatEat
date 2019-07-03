package com.example.myapplication.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private Context context;
    private String restaurant = "";

    private List<CartFood> cartFoods = new ArrayList<>();
    private List<CartUser> cartUsers = new ArrayList<>();

    public Cart(Context context) {
        this.context = context;
    }

    public void save() {
        try {
            File cartFile = new File("SeatEat_Cart");
            cartFile.createNewFile();
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
            File cartFile = new File("SeatEat_Cart");
            cartFile.createNewFile();
            FileInputStream fis = context.openFileInput("SeatEat_Cart");
            ObjectInputStream is = new ObjectInputStream(fis);
            Cart oldCart = (Cart) is.readObject();
            this.restaurant = oldCart.getRestaurant();
            this.cartFoods = oldCart.getCartFoods();
            this.cartUsers = oldCart.getCartUsers();
            is.close();
            fis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("SeatEat_Cart cannot be created or saved");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Cart class not found");
        }
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

    public void addCartFood(String id, String name, double price, int quantity, List<String> users) {
        this.cartFoods.add(new CartFood(id, name, price, quantity, users));
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

    private class CartFood implements Serializable {
        private String id;
        private String name;
        private double price;
        private int quantity;
        private List<String> users;

        CartFood(String id, String name, double price, int quantity, List<String> users) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
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
            return quantity;
        }

        public List<String> getUsers() {
            return users;
        }
    }

    private class CartUser implements Serializable{
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
