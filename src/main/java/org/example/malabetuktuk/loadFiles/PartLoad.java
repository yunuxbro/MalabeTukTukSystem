package org.example.malabetuktuk.loadFiles;

import org.example.malabetuktuk.blueprints.Part;

import java.io.*;
import java.util.ArrayList;

public class PartLoad {
    private static ArrayList<Part> inventoryList = new ArrayList<Part>();

    public static void loadItems(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("[,;|]");

                if (parts.length >= 7) {
                    String partId = parts[0].trim();
                    String partName = parts[1].trim();
                    String partBrand = parts[2].trim();
                    double price = parsePrice(parts[3].trim());
                    int quantity = parseQuantity(parts[4].trim());
                    String partType = parts[5].trim().toUpperCase();
                    String addedDate = cleanDate(parts[6].trim());
                    String imagePath = (parts.length >= 8) ? parts[7].trim() : "null";

                    if (partId.isEmpty() || partName.isEmpty()) {
                        continue;
                    }

                    Part newPart = new Part(partId, partName, partBrand, price, quantity, partType, addedDate, imagePath);
                    inventoryList.add(newPart);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    public static void saveItems(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Part part : inventoryList) {
                String line = part.getPartId() + "," +
                        part.getPartName() + "," +
                        part.getPartBrand() + "," +
                        part.getPartPrice() + "," +
                        part.getPartQuantity() + "," +
                        part.getPartType() + "," +
                        part.getAddedDate() + "," +
                        part.getImagePath();

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static String cleanDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty() || rawDate.equalsIgnoreCase("null")) {
            return "2026-01-01";
        }

        if (rawDate.contains(",")) {
            return rawDate.split(",")[0].trim();
        }
        if (rawDate.contains(" ")) {
            return rawDate.split(" ")[0].trim();
        }

        return rawDate;
    }

    private static double parsePrice(String priceStr) {
        try {
            String clean = priceStr.replace("Rs.", "").replace("Rs", "").trim();
            double p = Double.parseDouble(clean);
            return p >= 0 ? p : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int parseQuantity(String qtyStr) {
        try {
            int q = Integer.parseInt(qtyStr.trim());
            return q >= 0 ? q : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static ArrayList<Part> getInventoryList() {
        return inventoryList;
    }
}