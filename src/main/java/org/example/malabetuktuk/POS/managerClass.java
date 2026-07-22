package org.example.malabetuktuk.POS;

import org.example.malabetuktuk.blueprints.Cartitems;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        boolean hasEngine = false;
        boolean hasElectrical = false;

        for (Cartitems item : activeCart) {
            String type = item.getPart().getPartType().trim().toUpperCase();
            if (type.equals("ENGINE")) {
                hasEngine = true;
            }
            if (type.equals("ELECTRICAL")) {
                hasElectrical = true;
            }

            int qty = item.getOrderedQuantity();
            double lineTotal = item.getPart().getPartPrice() * qty;

            if (qty >= 3) {
                lineTotal = lineTotal * 0.95;
            }

            subtotal += lineTotal;
        }

        if (hasEngine && hasElectrical) {
            subtotal = subtotal * 0.90;
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

    public static boolean processCheckout() {
        if (!cartvalidation(activeCart)) {
            return false;
        }

        double finalTotal = calculateSubtotal();

        for (Cartitems item : activeCart) {
            logAuditAction("CHECKOUT",
                    item.getPartId(),
                    item.getOrderedQuantity(),
                    "Sold unit price: Rs. " + item.getPart().getPartPrice());
        }

        PartLoad.saveItems("data/updated_data_legacy.txt");

        activeCart.clear();
        return true;
    }

    public static void logAuditAction(String actionType, String itemCode, int quantity, String description) {
        try {
            FileWriter fw = new FileWriter("data/audit_log.txt", true);
            PrintWriter pw = new PrintWriter(fw);

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(dtf);

            String qtyString = (quantity >= 0) ? String.valueOf(quantity) : "N/A";
            String codeString = (itemCode != null && !itemCode.isEmpty()) ? itemCode : "N/A";

            pw.println(String.format("[%s] ACTION: %s | ITEM: %s | QTY: %s | DETAILS: %s",
                    timestamp, actionType, codeString, qtyString, description));

            pw.close();
            fw.close();
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }
}