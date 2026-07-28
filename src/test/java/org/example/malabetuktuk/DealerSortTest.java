package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Dealer;
import org.example.malabetuktuk.sorts.dealerSort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DealerSortTest {

    private ArrayList<Dealer> dealers;

    @BeforeEach
    void setUp() {
        dealers = new ArrayList<>();
        dealers.add(new Dealer("D001", "Kandy Motors",0721112222, "Kandy"));
        dealers.add(new Dealer("D002", "Colombo Auto Parts",0721112222, "Colombo"));
        dealers.add(new Dealer("D003", "Galle Spares", 0740711002,"Galle"));
    }

    @Test
    void testSortsDealersByCityAlphabetically() {
        dealerSort.bubbleSortByCity(dealers);

        assertEquals("Colombo", dealers.get(0).getDealerCity());
        assertEquals("Galle", dealers.get(1).getDealerCity());
        assertEquals("Kandy", dealers.get(2).getDealerCity());
    }
}