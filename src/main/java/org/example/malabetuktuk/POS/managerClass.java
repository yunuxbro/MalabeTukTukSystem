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
        ArrayList<Part> uniParts =  new ArrayList<>();
        ArrayList<Integer> quantity = new ArrayList<>();
        double subtotal = 0.0;
        boolean hasEngine = false;
        boolean hasElectrical = false;
        for (Cartitems item : activeCart) {
            String type = item.getpartType().trim().toUpperCase();
            if (type.equals("ENGINE")) {
                hasEngine = true;
            }
            if (type.equals("ELECTRICAL")) {
                hasElectrical = true;
            }
            int index = -1;
            for (int i = 0; i < quantity.size(); i++) {
                if (quantity.get(i) == item.getOrderedQuantity()) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                quantity.set(index, quantity.get(index) + 1);
            }
            else {
                uniParts.add(item.getPart());
                quantity.add(1);

            }
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