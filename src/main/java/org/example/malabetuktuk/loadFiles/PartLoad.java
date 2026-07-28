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

        inventoryList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String normalizedLine = normalizeDelimiters(line);
                String[] parts = normalizedLine.split(";");

                ArrayList<String> cleaned = new ArrayList<>();
                for (String p : parts) {
                    cleaned.add(cleanField(p));
                }

                if (cleaned.size() >= 7) {
                    String partId = cleaned.get(0);
                    String partName = cleaned.get(1);
                    String partBrand = cleaned.get(2);
                    double price = parsePrice(cleaned.get(3));
                    int quantity = parseQuantity(cleaned.get(4));
                    String partType = cleaned.get(5).toUpperCase();
                    String addedDate = cleaned.get(6);

                    String imagePath = "null";
                    if (cleaned.size() >= 8) {
                        imagePath = cleaned.get(7);
                    }

                    if (partId.equals("null") || partName.equals("null")) {
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

    private static String normalizeDelimiters(String line) {
        line = line.replace('|', ';');
        line = line.replaceAll("(?i)(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+(\\d{1,2}),\\s*(\\d{4})", "$1 $2__DATECOMMA__$3");
        line = line.replace(',', ';');
        line = line.replace("__DATECOMMA__", ", ");

        return line;
    }

    public static void saveItems(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Part part : inventoryList) {
                String line = cleanField(part.getPartId()) + "; " +
                        cleanField(part.getPartName()) + "; " +
                        cleanField(part.getPartBrand()) + "; " +
                        part.getPartPrice() + "; " +
                        part.getPartQuantity() + "; " +
                        cleanField(part.getPartType()) + "; " +
                        cleanField(part.getAddedDate()) + "; " +
                        cleanField(part.getImagePath());

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public static String cleanField(String val) {
        if (val == null || val.trim().isEmpty() || val.trim().equalsIgnoreCase("null")) {
            return "null";
        }
        return val.trim();
    }

    private static double parsePrice(String priceStr) {
        try {
            String clean = priceStr.replace("Rs.", "").replace("Rs", "").trim();
            double p = Double.parseDouble(clean);
            if (p >= 0) {
                return p;
            }
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static int parseQuantity(String qtyStr) {
        try {
            int q = Integer.parseInt(qtyStr.trim());
            if (q >= 0) {
                return q;
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static ArrayList<Part> getInventoryList() {
        return inventoryList;
    }
}