package org.example.malabetuktuk.loadFiles;

import org.example.malabetuktuk.blueprints.Part;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class PartLoad {
    private static ArrayList<Part> inventoryList = new ArrayList<Part>();

    public static void loadItems(String fileName) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.trim().split("[,;|]", -1);

                if (parts.length >= 9) {
                    String partId = cleanField(parts[0]);
                    String partName = cleanField(parts[1]);
                    String partBrand = cleanField(parts[2]);

                    double price = parsePrice(parts[3]);
                    int quantity = parseQuantity(parts[4]);

                    String partType = cleanField(parts[5]).toUpperCase();
                    String rawDate = parts[6].trim() + ", " + parts[7].trim();
                    String addedDate = normalizeDate(rawDate);
                    String imagePath = cleanField(parts[8]);

                    Part newPart = new Part(partId, partName, partBrand, price, quantity, partType, addedDate, imagePath);
                    inventoryList.add(newPart);
                }
                else if (parts.length == 8) {
                    String partId = cleanField(parts[0]);
                    String partName = cleanField(parts[1]);
                    String partBrand = cleanField(parts[2]);

                    double price = parsePrice(parts[3]);
                    int quantity = parseQuantity(parts[4]);

                    String partType = cleanField(parts[5]).toUpperCase();
                    String addedDate = normalizeDate(parts[6].trim());
                    String imagePath = cleanField(parts[7]);

                    Part newPart = new Part(partId, partName, partBrand, price, quantity, partType, addedDate, imagePath);
                    inventoryList.add(newPart);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private static String cleanField(String val) {
        if (val == null || val.trim().isEmpty()) {
            return "null";
        }
        return val.trim();
    }

    private static double parsePrice(String priceStr) {
        if (priceStr == null) return 0.0;
        String clean = priceStr.trim().replace("Rs.", "").replace("Rs", "").trim();
        if (clean.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static int parseQuantity(String qtyStr) {
        if (qtyStr == null) return 0;
        String clean = qtyStr.trim();
        if (clean.isEmpty()) return 0;
        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String normalizeDate(String inputDate) {
        if (inputDate == null || inputDate.trim().isEmpty() || inputDate.trim().equalsIgnoreCase("null")) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
        }

        String str = inputDate.trim();

        String[] formats = {
                "yyyy-MM-dd, HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy/MM/dd, HH:mm",
                "yyyy/MM/dd HH:mm",
                "dd-MM-yyyy HH:mm",
                "dd/MM/yyyy HH:mm",
                "yyyy-MM-dd",
                "yyyy/MM/dd",
                "dd-MM-yyyy",
                "dd/MM/yyyy"
        };

        for (String fmt : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
                if (fmt.contains("HH")) {
                    LocalDateTime dt = LocalDateTime.parse(str, formatter);
                    return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
                } else {
                    java.time.LocalDate ld = java.time.LocalDate.parse(str, formatter);
                    return ld.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
                }
            } catch (DateTimeParseException ignored) {
            }
        }

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
    }

    public static ArrayList<Part> getInventoryList() {
        return inventoryList;
    }

    public static void saveItems(String fileName) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));

            for (Part part : inventoryList) {
                String line = part.getPartId() + "," +
                        part.getPartName() + "," +
                        part.getPartBrand() + "," +
                        String.format("%.2f", part.getPartPrice()) + "," +
                        part.getPartQuantity() + "," +
                        part.getPartType() + "," +
                        part.getAddedDate() + "," +
                        part.getImagePath();

                bw.write(line);
                bw.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) {
        loadItems("data/inventory_legacy.txt");
        saveItems("data/updated_data_legacy.txt");
    }
}