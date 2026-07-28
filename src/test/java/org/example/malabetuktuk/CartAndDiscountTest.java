package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Cartitems;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.POS.managerClass; // Update to your actual cart class / controller
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartAndDiscountTest {

    @BeforeEach
    void setUp() {
        if (managerClass.getCart() != null) {
            managerClass.getCart().clear();
        }
    }

    @Test
    void testBulkDiscountAppliedForQuantityThreeOrMore() {
        // Base: 3 * 1000.0 = 3000.0. 5% bulk discount = 2850.0
        Part brake = new Part("P001", "Brake Pad", "TVS", 1000.0, 10, "BRAKE", "2026-01-01", "null", 5);
        Cartitems cartItem = new Cartitems(brake, 3);
        managerClass.getCart().add(cartItem);

        double calculatedTotal = managerClass.calculateSubtotal();
        assertEquals(2850.0, calculatedTotal, 0.001);
    }

    @Test
    void testSynergyDiscountAppliedWhenEngineAndElectricalPresent() {
        // Engine (1000) + Electrical (1000) = 2000. 10% synergy discount = 1800.0
        Part enginePart = new Part("P001", "Piston Ring", "TVS", 1000.0, 10, "ENGINE", "2026-01-01", "null", 5);
        Part electricalPart = new Part("P002", "Spark Plug", "Bosch", 1000.0, 10, "ELECTRICAL", "2026-01-01", "null", 5);

        managerClass.getCart().add(new Cartitems(enginePart, 1));
        managerClass.getCart().add(new Cartitems(electricalPart, 1));

        double calculatedTotal = managerClass.calculateSubtotal();
        assertEquals(1800.0, calculatedTotal, 0.001);
    }
}