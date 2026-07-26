package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.POS.LowStock;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LowStockAndCartTest {

    @BeforeEach
    void setUp() {
        PartLoad.getInventoryList().clear();
        PartLoad.getInventoryList().add(new Part("P001", "Oil Filter", "TVS", 600.0, 2, "ENGINE", "2026-01-01", "null", 5));
        PartLoad.getInventoryList().add(new Part("P002", "Side Mirror", "Bajaj", 1200.0, 10, "BODY", "2026-01-01", "null", 4));
    }

    @Test
    void testDetectsLowStockItem() {
        ArrayList<Part> lowStockParts = LowStock.getLowStockParts();
        assertEquals(1, lowStockParts.size());
        assertEquals("P001", lowStockParts.get(0).getPartId());
    }

    @Test
    void testDeductStockOnPurchase() {
        Part part = PartLoad.getInventoryList().get(1);
        part.setPartQuantity(part.getPartQuantity() - 3);
        assertEquals(7, part.getPartQuantity());
    }
}