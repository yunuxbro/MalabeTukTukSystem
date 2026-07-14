package org.example.malabetuktuk.POS;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

import java.util.ArrayList;

public class LowStock {
    ArrayList<Part> lowStockParts = new ArrayList<>();
    ArrayList<Part> allParts = PartLoad.getInventoryList();
}
