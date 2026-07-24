package org.example.malabetuktuk.CRUD;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

import java.util.ArrayList;

public class UpdateItem {

    public static String executeUpdate(String partId, String name, String brand, String priceStr, String qtyStr, String type, String imgPath, String filePath) {
        if (name == null || name.trim().isEmpty() ||
                brand == null || brand.trim().isEmpty() ||
                type == null || type.trim().isEmpty()) {
            return "All text fields are required!";
        }

        double price;
        int qty;

        try {
            price = Double.parseDouble(priceStr.trim());
            qty = Integer.parseInt(qtyStr.trim());
            if (price < 0 || qty < 0) {
                return "Price and Quantity must be positive numbers!";
            }
        } catch (NumberFormatException e) {
            return "Invalid number format for Price or Quantity!";
        }

        ArrayList<Part> list = PartLoad.getInventoryList();
        Part targetPart = null;

        for (Part p : list) {
            if (p.getPartId().equalsIgnoreCase(partId.trim())) {
                targetPart = p;
                break;
            }
        }

        if (targetPart == null) {
            return "Item with ID " + partId + " not found!";
        }

        targetPart.setPartName(name.trim());
        targetPart.setPartBrand(brand.trim());
        targetPart.setPartPrice(price);
        targetPart.setPartQuantity(qty);
        targetPart.setPartType(type.trim());
        targetPart.setImagePath(imgPath.trim());

        PartLoad.saveItems(filePath);
        return "SUCCESS";
    }
}