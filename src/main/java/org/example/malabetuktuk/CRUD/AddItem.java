package org.example.malabetuktuk.CRUD;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import java.util.ArrayList;

public class AddItem {
    ArrayList<Part> Inventory = PartLoad.getInventoryList();
    public static boolean executeAdd(String partId, String partName, String partBrand, double partPrice, int partQuantity, String partType, String addedDate,String imagePath,String filePath){
        try{
            Part newPart = new Part(partId,partName,partBrand,partPrice,partQuantity,partType,addedDate,imagePath);
            PartLoad.getInventoryList().add(newPart);
            PartLoad.saveItems(filePath);
        }catch(Exception e){
            return false;
        }
        return false;
    }

    
}
