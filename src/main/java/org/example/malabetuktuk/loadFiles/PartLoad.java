package org.example.malabetuktuk.loadFiles;

import org.example.malabetuktuk.blueprints.Part;

import java.io.*;
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
                    String partId = parts[0].trim();

                    String partName = parts[1].trim();
                    if (partName.isEmpty()) {
                        partName = "null";
                    }

                    String partBrand = parts[2].trim();
                    if (partBrand.isEmpty()) {
                        partBrand = "null";
                    }

                    String priceStr = parts[3].trim().replace("Rs.", "").replace("Rs", "").trim();
                    double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);

                    String qtyStr = parts[4].trim();
                    int quantity = qtyStr.isEmpty() ? 0 : Integer.parseInt(qtyStr);

                    String partType = parts[5].trim().toUpperCase();
                    String addedDate = parts[6].trim() + ", " + parts[7].trim();
                    String imagePath = "null";

                    Part newPart = new Part(partId, partName, partBrand, price, quantity, partType, addedDate, imagePath);
                    inventoryList.add(newPart);
                }
                else if (parts.length == 8) {
                    String partId = parts[0].trim();

                    String partName = parts[1].trim();
                    if (partName.isEmpty()) {
                        partName = "null";
                    }

                    String partBrand = parts[2].trim();
                    if (partBrand.isEmpty()) {
                        partBrand = "null";
                    }

                    String priceStr = parts[3].trim().replace("Rs.", "").replace("Rs", "").trim();
                    double price = 0.00;
                    if(priceStr.isEmpty()){
                        price = 0.00;
                    }
                    else{
                        price =  Double.parseDouble(priceStr);
                    }

                    String qtyStr = parts[4].trim();
                    int quantity = qtyStr.isEmpty() ? 0 : Integer.parseInt(qtyStr);

                    String partType = parts[5].trim().toUpperCase();
                    String addedDate = parts[6].trim();

                    String imagePath = parts[7].trim();
                    if (imagePath.isEmpty()) {
                        imagePath = "null";
                    }

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

    public static ArrayList<Part> getInventoryList() {
        return inventoryList;
    }

    public static void saveItems(String fileName) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new java.io.FileWriter(fileName));

            for (Part part : inventoryList) {
                String line = part.getPartId() + "," +
                        part.getPartName() + "," +
                        part.getPartBrand() + "," +
                        part.getPartPrice() + "," +
                        part.getPartQuantity(partQuantity) + "," +
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