package org.example.malabetuktuk.CRUD;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

public class AddItem {
    public static boolean executeAdd(String partId, String partName, String partBrand, double partPrice, int partQuantity, String partType, String addedDate, String imagePath, String filePath){
        try {
            Part newPart = new Part(partId, partName, partBrand, partPrice, partQuantity, partType, addedDate, imagePath);
            PartLoad.getInventoryList().add(newPart);
            PartLoad.saveItems(filePath);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}