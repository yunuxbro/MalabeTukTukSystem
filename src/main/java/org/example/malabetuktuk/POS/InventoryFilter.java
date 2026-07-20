package org.example.malabetuktuk.POS;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import java.util.ArrayList;

public class InventoryFilter {
    public static ArrayList<Part> filter(String partType, double minimumPrice, double maximumPrice, String keyword){
        ArrayList<Part> filteredList = new ArrayList<>();
        ArrayList<Part> allParts = PartLoad.getInventoryList();

        for (Part part : allParts) {
            if(partType != null && !partType.equals("") && !partType.equalsIgnoreCase("All")) {
                if(!part.getPartType().equalsIgnoreCase(partType)) {
                    continue;
                }
            }

            if(part.getPartPrice() <  minimumPrice || part.getPartPrice() > maximumPrice) {
                continue;
            }

            if (keyword != null && !keyword.trim().equals("")) {
                String lowerKey = keyword.toLowerCase().trim();
                boolean matchId = part.getPartId().toLowerCase().contains(lowerKey);
                boolean matchName = part.getPartName().toLowerCase().contains(lowerKey);
                boolean matchBrand = part.getPartBrand().toLowerCase().contains(lowerKey);

                if (!matchId && !matchName && !matchBrand) {
                    continue;
                }
            }
            filteredList.add(part);
        }


        return filteredList;
    }


}
