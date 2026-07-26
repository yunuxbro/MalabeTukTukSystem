package org.example.malabetuktuk.POS;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

import java.util.ArrayList;

public class LowStock {

    public static ArrayList<Part> getLowStockParts() {
        ArrayList<Part> lowStockList = new ArrayList<>();

        for (Part part : PartLoad.getInventoryList()) {
            if (part.getPartQuantity() <= part.getLowStockThreshold()) {
                lowStockList.add(part);
            }
        }

        return lowStockList;
    }
}