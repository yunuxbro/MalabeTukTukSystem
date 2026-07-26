package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.POS.InventoryFilter;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InventoryFilterTest {

    @BeforeEach
    void setUp() {
        PartLoad.getInventoryList().clear();
        PartLoad.getInventoryList().add(new Part("P001", "Piston Ring", "TVS", 1500.0, 10, "ENGINE", "2026-01-01", "null", 5));
        PartLoad.getInventoryList().add(new Part("P002", "Headlight Bulb", "Bajaj", 500.0, 20, "ELECTRICAL", "2026-01-01", "null", 5));
        PartLoad.getInventoryList().add(new Part("P003", "Brake Shoe", "TVS", 2500.0, 3, "BRAKE", "2026-01-01", "null", 2));
    }

    @Test
    void testFiltersByCategoryAndPrice() {
        ArrayList<Part> results = InventoryFilter.filter("ENGINE", 1000.0, 2000.0, "");
        assertEquals(1, results.size());
        assertEquals("P001", results.get(0).getPartId());
    }

    @Test
    void testSearchesByKeyword() {
        ArrayList<Part> results = InventoryFilter.filter("All", 0.0, 5000.0, "Piston");
        assertEquals(1, results.size());
        assertEquals("P001", results.get(0).getPartId());
    }
}