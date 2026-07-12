package org.example.malabetuktuk.CRUD;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

public class UpdateItem {
    public static boolean exeecuteUpdate(String partId, String partName, String partBrand, Double partPrice, int partQuantity, String partType, String addedDate, String imagePath , String filePath) {
        boolean result = false;
        for(Part part : PartLoad.getInventoryList()){
            if(part.getPartId()==partId){
                part.setPartName(partName);
                part.setPartBrand(partBrand);
                part.setPartPrice(partPrice);
                part.getPartQuantity(partQuantity);
                part.setPartType(partType);
                part.setAddedDate(addedDate);
                part.setImagePath(imagePath);

                result = true;
                break;
            }
        }
        if(result){
            PartLoad.saveItems(filePath);
            return true;

        }
        return false;
    }

}
