package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ParsingAndIOTest {

    private final String TEST_FILE = "data/test_inventory.txt";

    @BeforeEach
    void setUp() {
        PartLoad.getInventoryList().clear();
    }

    @Test
    void testParsesValidData() throws IOException {
        File file = new File(TEST_FILE);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write("P001,Piston,TVS,1500.0,10,ENGINE,2026-01-01,image.jpg,5\n");
        writer.close();

        PartLoad.loadItems(TEST_FILE);
        ArrayList<Part> list = PartLoad.getInventoryList();

        assertEquals(1, list.size());
        assertEquals("P001", list.get(0).getPartId());
        assertEquals("Piston", list.get(0).getPartName());
        assertEquals(1500.0, list.get(0).getPartPrice());

        file.delete();
    }

    @Test
    void testThrowsOnInvalidNumberFormat() {
        assertThrows(NumberFormatException.class, () -> Double.parseDouble("invalid_price"));
    }
}