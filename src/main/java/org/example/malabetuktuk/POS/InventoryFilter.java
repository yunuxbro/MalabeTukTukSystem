package org.example.malabetuktuk.POS;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import java.util.ArrayList;

public class InventoryFilter {
    public static ArrayList<Part> filter(String partType, double partPrice, String keyword){
        ArrayList<Part> filteredList = new ArrayList<>();
        ArrayList<Part> allParts = PartLoad.getInventoryList();
    }

}
