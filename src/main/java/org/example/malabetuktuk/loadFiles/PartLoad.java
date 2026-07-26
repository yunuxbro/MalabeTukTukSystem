package org.example.malabetuktuk.loadFiles;

import org.example.malabetuktuk.blueprints.Part;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                    String partId = cleanField(parts[0]);
                    String partName = cleanField(parts[1]);
                    String partBrand = cleanField(parts[2]);
                    double price = parsePrice(parts[3].trim());
                    int quantity = parseQuantity(parts[4].trim());
                    String partType = cleanField(parts[5]).toUpperCase();
                    String addedDate = normalizeDate(parts[6].trim());
                    String imagePath = (parts.length >= 8) ? cleanField(parts[7]) : "null";

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

    public static void saveItems(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Part part : inventoryList) {
                String line = cleanField(part.getPartId()) + "," +
                        cleanField(part.getPartName()) + "," +
                        cleanField(part.getPartBrand()) + "," +
                        part.getPartPrice() + "," +
                        part.getPartQuantity() + "," +
                        cleanField(part.getPartType()) + "," +
                        cleanField(part.getAddedDate()) + "," +
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

    public static String normalizeDate(String rawDate) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        if (rawDate == null || rawDate.isEmpty() || rawDate.equalsIgnoreCase("null")) {
            return today;
        }

        String str = rawDate.trim();
        if (str.contains(",")) {
            str = str.split(",")[0].trim();
        }
        if (str.contains(" ")) {
            str = str.split(" ")[0].trim();
        }

        str = str.replace("-", "/");

        String[] parts = str.split("/");
        if (parts.length == 3) {
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            if (year.length() == 4) {
                if (month.length() == 1) month = "0" + month;
                if (day.length() == 1) day = "0" + day;
                return year + "/" + month + "/" + day;
            } else if (day.length() == 4) {
                if (month.length() == 1) month = "0" + month;
                if (year.length() == 1) year = "0" + year;
                return day + "/" + month + "/" + year;
            }
        }

        return str;
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