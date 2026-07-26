package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.junit.jupiter.api.BeforeEach;


class ParsingAndIOTest {

    private final String TEST_FILE = "data/test_inventory.txt";

    @BeforeEach
    void setUp() {
        PartLoad.getInventoryList().clear();
    }


}