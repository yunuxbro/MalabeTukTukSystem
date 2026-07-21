package org.example.malabetuktuk.POS;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import java.util.ArrayList;

public class LowStock {

    public static ArrayList<Part> getLowStockParts(int barrier) {
        ArrayList<Part> lowStockList = new ArrayList<>();
        ArrayList<Part> allParts = PartLoad.getInventoryList();

        for (Part p : allParts) {
            if (p.getPartQuantity() < barrier) {
                lowStockList.add(p);
            }
        }
        return lowStockList;
    }
}