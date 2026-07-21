package org.example.malabetuktuk.POS;

import org.example.malabetuktuk.blueprints.Cartitems;
import org.example.malabetuktuk.blueprints.Part;
import java.util.ArrayList;

public class managerClass {

    private static final ArrayList<Cartitems> activeCart = new ArrayList<>();

    public static ArrayList<Cartitems> getCart() {
        return activeCart;
    }


    public static void addToCart(Part part) {
        for (Cartitems item : activeCart) {
            if (item.getPartId().equalsIgnoreCase(part.getPartId())) {
                item.setOrderedQuantity(item.getOrderedQuantity() + 1);
                return;
            }
        }
        activeCart.add(new Cartitems(part, 1));
    }


    public static void removeFromCart(Cartitems item) {
        Part originalPart = item.getPart();
        originalPart.setPartQuantity(originalPart.getPartQuantity() + item.getOrderedQuantity());
        activeCart.remove(item);
    }


    public static void clearCart() {
        activeCart.clear();
    }

    public static double calculateSubtotal() {
        double subtotal = 0.0;
        for (Cartitems item : activeCart) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }


    public static boolean cartvalidation(ArrayList<Cartitems> cart) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }
        for (Cartitems c : cart) {
            if (c.getOrderedQuantity() <= 0) {
                return false;
            }
        }
        return true;
    }
}