package org.example.malabetuktuk.CRUD;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.POS.managerClass;

import java.util.ArrayList;

public class AddItem {

    public static String executeAdd(String id, String name, String brand, String priceStr, String qtyStr, String type, String addedDate, String imagePath, String filePath) {
        if (!managerClass.validatePartData(id, name, brand, priceStr, qtyStr, type)) {
            return "Invalid input data. Ensure required fields are filled and numbers are valid positive values.";
        }

        ArrayList<Part> inventory = PartLoad.getInventoryList();
        for (Part p : inventory) {
            if (p.getPartId().equalsIgnoreCase(id.trim())) {
                return "Part ID already exists in system!";
            }
        }

        double price = Double.parseDouble(priceStr.trim());
        int quantity = Integer.parseInt(qtyStr.trim());

        Part newPart = new Part(id.trim(), name.trim(), brand.trim(), price, quantity, type.trim().toUpperCase(), addedDate, imagePath);
        inventory.add(newPart);

        managerClass.logAuditAction("ADD", id.trim(), quantity, "Added new part: " + name.trim());
        PartLoad.saveItems(filePath);

        return "SUCCESS";
    }
}