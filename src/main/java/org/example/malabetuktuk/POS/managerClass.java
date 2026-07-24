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
import java.util.List;

public class managerClass {

    private static final List<Cartitems> activeCart = new ArrayList<>();

    public static List<Cartitems> getCart() {
        return activeCart;
    }


    public static boolean validatePartData(String id, String name, String brand, String priceStr, String qtyStr, String type) {
        if (id == null || id.trim().isEmpty()) return false;
        if (name == null || name.trim().isEmpty()) return false;
        if (brand == null || brand.trim().isEmpty()) return false;
        if (type == null || type.trim().isEmpty()) return false;

        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            int qty = Integer.parseInt(qtyStr.trim());
            if (qty < 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    public static boolean validateSearchInputs(String minPriceStr, String maxPriceStr, String thresholdStr) {
        double minPrice = 0.0;
        double maxPrice = Double.MAX_VALUE;

        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                minPrice = Double.parseDouble(minPriceStr.trim());
                if (minPrice < 0) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr.trim());
                if (maxPrice < 0) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        if (minPrice > maxPrice) return false;

        if (thresholdStr != null && !thresholdStr.trim().isEmpty()) {
            try {
                int threshold = Integer.parseInt(thresholdStr.trim());
                if (threshold < 0) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public static boolean validateAddToCart(Part part, int requestedQty) {
        if (part == null) return false;
        if (requestedQty <= 0) return false;
        return part.getPartQuantity() >= requestedQty;
    }

    public static void addToCart(Part part) {
        if (part == null) return;

        for (int i = 0; i < activeCart.size(); i++) {
            Cartitems item = activeCart.get(i);
            if (item.getPart().getPartId().equalsIgnoreCase(part.getPartId())) {
                item.setOrderedQuantity(item.getOrderedQuantity() + 1);
                return;
            }
        }
        activeCart.add(new Cartitems(part, 1));
    }

    public static void removeFromCart(Cartitems item) {
        if (item == null) return;
        Part originalPart = item.getPart();
        originalPart.setPartQuantity(originalPart.getPartQuantity() + item.getOrderedQuantity());
        activeCart.remove(item);
    }

    public static void clearCart() {
        activeCart.clear();
    }


    public static double calculateSubtotal() {
        double total = 0.0;
        boolean hasEngine = false;
        boolean hasElectrical = false;

        for (int i = 0; i < activeCart.size(); i++) {
            Cartitems item = activeCart.get(i);
            double itemTotal = item.getPart().getPartPrice() * item.getOrderedQuantity();

            // 1. Bulk Discount: 5% off item subtotal if quantity >= 3
            if (item.getOrderedQuantity() >= 3) {
                itemTotal *= 0.95;
            }
            total += itemTotal;

            // Synergy Category check
            String cat = item.getPart().getPartType().trim().toUpperCase();
            if (cat.contains("ENGINE")) {
                hasEngine = true;
            }
            if (cat.contains("ELECTRICAL") || cat.contains("ELECTRIC")) {
                hasElectrical = true;
            }
        }

        // 2. Synergy Discount: 10% off total cart if Engine AND Electrical items are present
        if (hasEngine && hasElectrical) {
            total *= 0.90;
        }

        return total;
    }

    public static boolean cartvalidation(List<Cartitems> cart) {
        return cart != null && !cart.isEmpty();
    }

    public static boolean validatePaymentMethod(String paymentMethod) {
        return paymentMethod != null && !paymentMethod.trim().isEmpty() && !paymentMethod.equalsIgnoreCase("Select Payment");
    }

    public static boolean processCheckout() {
        if (!cartvalidation(activeCart)) {
            return false;
        }

        for (int i = 0; i < activeCart.size(); i++) {
            Cartitems item = activeCart.get(i);
            logAuditAction("CHECKOUT", item.getPart().getPartId(), item.getOrderedQuantity(),
                    "Purchased: " + item.getPart().getPartName());
        }

        PartLoad.saveItems("data/updated_data_legacy.txt");
        clearCart();
        return true;
    }


    public static void logAuditAction(String action, String partId, int quantity, String details) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/audit_log.txt", true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(String.format("[%s] ACTION: %s | PART_ID: %s | QTY: %d | DETAILS: %s",
                    timestamp, action, partId, quantity, details));
        } catch (IOException e) {
            System.err.println("Failed to write to audit log: " + e.getMessage());
        }
    }
}