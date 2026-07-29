package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.sorts.itemSort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ManualSortTest {

    @BeforeEach
    void setUp() {
        PartLoad.getInventoryList().clear();
        PartLoad.getInventoryList().add(new Part("P003", "Clutch Cable", "TVS", 800.0, 15, "BODY", "2026-01-01", "null", 5));
        PartLoad.getInventoryList().add(new Part("P001", "Air Filter", "Bajaj", 400.0, 8, "ENGINE", "2026-01-01", "null", 3));
        PartLoad.getInventoryList().add(new Part("P002", "Spark Plug", "Bosch", 1200.0, 2, "ELECTRICAL", "2026-01-01", "null", 5));
    }

    @Test
    void testSortsInventoryManually() {
        itemSort.sortInventory();
        ArrayList<Part> list = PartLoad.getInventoryList();

        // Primary sort is Category (BODY < ELECTRICAL < ENGINE)
        assertEquals("P003", list.get(0).getPartId()); // BODY
        assertEquals("P002", list.get(1).getPartId()); // ELECTRICAL
        assertEquals("P001", list.get(2).getPartId()); // ENGINE
    }
}