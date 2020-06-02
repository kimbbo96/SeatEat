package com.example.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import java.time.Duration;
import java.time.Instant;

public class Cart implements Serializable {
    public static final long serialVersionUID = 42L;
    private transient Context context;
    private static int ordNum = 1;

    private  Instant timestamp = null;
//    private static ScheduledThreadPoolExecutor timer = null;
    private boolean fake = false;
    private boolean[] doRefresh = {false};
    private String userId;
    private List<CartFood> cartFoods = new ArrayList<>();
    private List<CartUser> cartUsers = new ArrayList<>();

    public Cart(@NonNull Context context) {
        this.context = context;

        SharedPreferences preferences = context.getSharedPreferences("loginref", MODE_PRIVATE);
        userId = preferences.getString("nome", "");


        Instant start = Instant.now(); // set initial timestamp
        setTimestamp(start);
    }

    public void save() {
        try {
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
            FileInputStream fis = context.openFileInput("SeatEat_Cart");
            ObjectInputStream is = new ObjectInputStream(fis);
            Cart oldCart = (Cart) is.readObject();
            this.cartFoods = oldCart.getCartFoods();
            this.cartUsers = oldCart.getCartUsers();
            if (! cartFoods.isEmpty()) {
                ordNum = Integer.max(ordNum, cartFoods.stream().mapToInt(CartFood::getOrdNum).max().getAsInt());
                fake = oldCart.fake;  // TODO solo per DEBUG! togliere!!!
            }
            is.close();
            fis.close();
        } catch (FileNotFoundException | ClassCastException ex) {
            System.out.println("Creating new cart");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("SeatEat_Cart cannot be created or saved");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Cart class not found");
        }
    }

//    public void shutDown() {
//        timer.shutdown();
//    }

    /**
     * Clear the content of the cart, but doesn't write it on persistent storage.
     */
    public void clear() {
        ordNum = 1;
        cartUsers.clear();
        cartFoods.clear();
//        timer.shutdown();
        save();
    }

    /**
     * Update the order number, when the current order is sent
     */
    public void newOrder() {
        ordNum = ordNum + 1;
    }


    public  Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getTotal() {
        return cartFoods.stream().mapToDouble(cf -> cf.getPrice() * cf.getQuantity()).sum();
    }

    public double getTotal(String user) {
        return cartFoods.stream().mapToDouble(cf -> {
                    if (cf.getUser().equals(user))
                        return cf.getPrice() * cf.getQuantity();
                    else return 0;
                }).sum();
    }

    public double getTotalCheckout() {
        return cartFoods.stream().mapToDouble(cf -> {
            if (cf.ordNum < ordNum)
                return cf.getPrice() * cf.getQuantity();
            else return 0;
        }).sum();
    }

    public double getTotalShares() {
        return cartUsers.stream().mapToDouble(CartUser::getShare).sum();
    }

    public int getOrdNum() {
        return ordNum;
    }

    public List<CartFood> getCartFoods() {
        return cartFoods;
    }

    public List<CartFood> getCartFoods(int ordNumber) {
        List<CartFood> ordCartFoods = new ArrayList<>();
        for (CartFood cf : cartFoods) {
            if (cf.ordNum == ordNumber) {
                boolean existing = false;
                for (CartFood ocf : ordCartFoods) {
                    if (cf.id.equals(ocf.id) && cf.note.equals(ocf.note)) {
                        existing = true;
                        ocf.quantity = ocf.quantity + cf.quantity;
                        break;
                    }
                }
                if (!existing) {
                    ordCartFoods.add(cf.deepCopy());
                }
            }
        }
        return ordCartFoods;
    }

    public List<CartFood> getOldCartFoods(int ordNumber) {
        List<CartFood> oldCartFoods = new ArrayList<>();
        for (int i = 1; i < ordNumber; i++) {
            for (CartFood cf : getCartFoods(i)) {
                boolean existing = false;
                for (CartFood ocf : oldCartFoods) {
                    if (cf.id.equals(ocf.id) && cf.note.equals(ocf.note)) {
                        existing = true;
                        ocf.quantity = ocf.quantity + cf.quantity;
                        break;
                    }
                }
                if (! existing) {
                    oldCartFoods.add(new CartFood(cf.id, cf.name, cf.price, cf.user, cf.quantity,
                            cf.note, 0, cf.shortDescr, cf.longDescr, cf.image));
                }
            }
        }
        return oldCartFoods;
    }

    public List<CartFood> getCartFoods(String user) {
        List<CartFood> myCartFoods = new ArrayList<>();
        for (CartFood cf : cartFoods) {
            if (cf.user.equals(user)) {
                myCartFoods.add(cf.deepCopy());
            }
        }
        return myCartFoods;
    }

    public List<CartFood> getCartFoods(int ordNumber, String user) {
        List<CartFood> ordCartFoods = new ArrayList<>();
        for (CartFood cf : cartFoods) {
            if (cf.ordNum == ordNumber && cf.user.equals(user)) {
                ordCartFoods.add(cf.deepCopy());
            }
        }
        return ordCartFoods;
    }

    public List<CartFood> getOldCartFoods(int ordNumber, String user) {
        List<CartFood> oldCartFoods = new ArrayList<>();
        for (int i = 1; i < ordNumber; i++) {
            for (CartFood cf : getCartFoods(i, user)) {
                boolean existing = false;
                for (CartFood ocf : oldCartFoods) {
                    if (cf.id.equals(ocf.id) && cf.user.equals(ocf.user) && cf.note.equals(ocf.note)) {
                        existing = true;
                        ocf.quantity = ocf.quantity + cf.quantity;
                        break;
                    }
                }
                if (! existing) {
                    oldCartFoods.add(new CartFood(cf.id, cf.name, cf.price, cf.user,cf.quantity, cf.note, 0, cf.shortDescr, cf.longDescr, cf.image));
                }
            }
        }
        return oldCartFoods;
    }

    public void setCartFoods(List<CartFood> cartFoods) {
        this.cartFoods = cartFoods;
    }

    public static List<CartFood> getOthersCartFoods(List<CartFood> myCartFoods, String user) {
        List<CartFood> otherCartFoods = new ArrayList<>();
        for (CartFood cf : myCartFoods) {
            if (! cf.user.equals(user)) {
                otherCartFoods.add(cf.deepCopy());
            }
        }
        return otherCartFoods;
    }

    public static List<CartFood> getMyCartFoods(List<CartFood> myCartFoods, String user) {
        List<CartFood> otherCartFoods = new ArrayList<>();
        for (CartFood cf : myCartFoods) {
            if (cf.user.equals(user)) {
                otherCartFoods.add(cf.deepCopy());
            }
        }
        return otherCartFoods;
    }

    public CartFood addCartFood(String id, String name, double price, String userID, String note,
                                String shortDescr, String longDescr, String image) {
        CartFood cartFood = null;
        boolean existing = false;
        for (CartFood cf : cartFoods) {
            if (cf.id.equals(id) && cf.user.equals(userID) && cf.note.equals(note) && cf.ordNum == ordNum) {
                existing = true;
                cf.incrementQuantity();
                cartFood = cf;
                break;
            }
        }
        if (! existing) {
            cartFood = new CartFood(id, name, price, userID,1, note, ordNum, shortDescr, longDescr, image);
            this.cartFoods.add(cartFood);
        }
        return cartFood;
    }

    public CartFood removeCartFood(String id, String userID, String note) {
        CartFood cartFood = null;
        Iterator itr = cartFoods.iterator();
        while (itr.hasNext()) {
            CartFood cf = (CartFood) itr.next();
            if (cf.id.equals(id) && cf.user.equals(userID) && cf.note.equals(note)) {
                cf.decrementQuantity();
                if (cf.quantity == 0) {
                    itr.remove();
                }
                cartFood = cf;
            }
        }
        return cartFood;
    }

    public List<CartUser> getCartUsers() {
        return cartUsers;
    }

    public void setCartUsers(List<CartUser> cartUsers) {
        this.cartUsers = cartUsers;
    }

    public void addCartUser(String name, boolean isTabletop) {
        this.cartUsers.add(new CartUser(name, isTabletop));
    }

    public String getCartUsersNames() {
        SharedPreferences preferencesUser = context.getSharedPreferences("loginref", MODE_PRIVATE);
        String userId = preferencesUser.getString("nome", "");

        System.out.println("GETCARTUSERNAMES " + cartUsers);

        StringBuilder usersNames = new StringBuilder();
        for (CartUser cu : cartUsers) {
            if (cu.name.equals(userId)) {
                if (cu.isTabletop) {
                    usersNames.append("TU, ");
                } else {
                    usersNames.append("tu, ");
                }
            } else {
                if (cu.isTabletop) {
                    usersNames.append(cu.name.toUpperCase() + ", ");
                } else {
                    usersNames.append(cu.name + ", ");
                }
            }

        }
        String res = usersNames.toString();
        return res.isEmpty() ? null : res.substring(0, res.length()-2);
    }

    public double getShare(String user) {
        for (CartUser cu : cartUsers) {
            if (cu.name.equals(user)) {
                return cu.share;
            }
        }
        return -1;
    }

    public void setShare(String user, double share) {
        for (CartUser cu : cartUsers) {
            if (cu.name.equals(user)) {
                cu.setShare(share);
            }
        }
    }

    public int getFoodQuantity(String foodId, String userId) {
        int quantity = 0;
        for (CartFood cf : cartFoods) {
            if (cf.id.equals(foodId) && cf.user.equals(userId)) {
                quantity += cf.quantity;
            }
        }
        return quantity;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "context=" + context +
                ", ordNum=" + ordNum +
                ", cartFoods=" + cartFoods +
                ", cartUsers=" + cartUsers +
                '}';
    }

    public void fakeCart() {
        if (! fake) {
            cartFoods.add(new CartFood("3", "nuvolette di drago", 0.1, "d", 5, "", 1, "", "", ""));
            cartFoods.add(new CartFood("4", "spiedini di gambeli", 5, "e", 1, "", 2, "", "", ""));
            cartFoods.add(new CartFood("4", "spiedini di gambeli", 5, "d", 3, "", 3, "", "", ""));

            addCartFood("1", "pollo cloccante piccante", 1000, "b", "bono", "", "", "");
            addCartFood("1", "pollo cloccante piccante", 1000, "b", "bono", "", "", "");
            addCartFood("1", "pollo cloccante piccante", 1000, "c", "bono", "", "", "");
            addCartFood("1", "pollo cloccante piccante", 1000, "c", "", "", "", "");
            addCartFood("2", "noodles", 500, "b", "", "", "", "");
            addCartFood("2", "noodles", 500, "c", "", "", "", "");

            addCartUser("a", true);
            addCartUser("b", false);
            addCartUser("c", false);
            addCartUser("d", false);
            addCartUser("e", false);

            fake = true;
            System.out.println("FAKE!");
        }
    }

    public static class CartFood implements Serializable {
        public static final long serialVersionUID = 42L;
        private String id;
        private String name;
        private double price;
        private String user;
        private int quantity;
        private String note;
        private int ordNum;
        private String shortDescr;
        private String longDescr;
        private String image;

        private CartFood(String id, String name, double price, String user, int quantity, String note,
                         int ordNum, String shortDescr, String longDescr, String image) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.user = user;
            this.quantity = quantity;
            this.note = note;
            this.ordNum = ordNum;
            this.shortDescr = shortDescr;
            this.longDescr = longDescr;
            this.image = image;
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

        public String getUser() {
            return user;
        }

        public int getQuantity() {
            return quantity;
        }

        private void incrementQuantity() {
            quantity = quantity + 1;
        }

        private void decrementQuantity() {
            quantity = quantity - 1;
        }

        public String getNote() {
            return note;
        }

        public int getOrdNum() {
            return ordNum;
        }

        public String getShortDescr() {
            return shortDescr;
        }

        public String getLongDescr() {
            return longDescr;
        }

        public String getImage() {
            return image;
        }

        public CartFood deepCopy() {
            return new CartFood(id, name, price, user, quantity, note, ordNum, shortDescr, longDescr, image);
        }

        @Override
        public String toString() {
            return "CartFood{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    ", user='" + user + '\'' +
                    ", quantity=" + quantity +
                    ", note='" + note + '\'' +
                    ", ordNum=" + ordNum +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CartFood cartFood = (CartFood) o;
            return Double.compare(cartFood.price, price) == 0 &&
                    quantity == cartFood.quantity &&
                    ordNum == cartFood.ordNum &&
                    id.equals(cartFood.id) &&
                    name.equals(cartFood.name) &&
                    user.equals(cartFood.user) &&
                    note.equals(cartFood.note) &&
                    shortDescr.equals(cartFood.shortDescr) &&
                    longDescr.equals(cartFood.longDescr) &&
                    image.equals(cartFood.image);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, price, user, quantity, note, ordNum, shortDescr, longDescr, image);
        }
    }

    public static class CartUser implements Serializable{
        public static final long serialVersionUID = 42L;
//        private String id;
        private String name;
        private boolean isTabletop;
        private Double share = 0.0;

        private CartUser(String name, boolean isTabletop) {
//            this.id = id;
            this.name = name;
            this.isTabletop = isTabletop;
        }

        public void setShare(Double share) { this.share = share; }

//        public String getId() {
//            return id;
//        }

        public String getName() {
            return name;
        }

        public boolean isTabletop() {
            return isTabletop;
        }

        public Double getShare() { return share; }

        @Override
        public String toString() {
            return "CartUser{" +
//                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", isTabletop=" + isTabletop +
                    '}';
        }
    }
}
