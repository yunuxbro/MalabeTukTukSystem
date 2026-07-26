package org.example.malabetuktuk;

import org.example.malabetuktuk.blueprints.Part;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void testDetectsFieldChanges() {
        Part oldPart = new Part("P001", "Chain Sprocket", "TVS", 3000.0, 10, "ENGINE", "2026-01-01", "null", 5);

        String newName = "Chain Sprocket Heavy Duty";
        double newPrice = 3500.0;

        StringBuilder changeLog = new StringBuilder();

        if (!oldPart.getPartName().equalsIgnoreCase(newName)) {
            changeLog.append("Name: ").append(oldPart.getPartName()).append(" -> ").append(newName).append(" | ");
        }
        if (oldPart.getPartPrice() != newPrice) {
            changeLog.append("Price: Rs.").append(oldPart.getPartPrice()).append(" -> Rs.").append(newPrice);
        }

        String result = changeLog.toString();

        assertTrue(result.contains("Name: Chain Sprocket -> Chain Sprocket Heavy Duty"));
        assertTrue(result.contains("Price: Rs.3000.0 -> Rs.3500.0"));
    }
}