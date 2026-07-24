package org.example.malabetuktuk.CRUD;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.POS.managerClass;

import java.util.ArrayList;

public class DeleteItem {

    public static boolean executeDelete(String partId, String filePath) {
        ArrayList<Part> partList = PartLoad.getInventoryList();
        Part targetPart = null;

        for (Part p : partList) {
            if (p.getPartId().equalsIgnoreCase(partId)) {
                targetPart = p;
                break;
            }
        }

        if (targetPart != null) {
            managerClass.logAuditAction(
                    "DELETE",
                    targetPart.getPartId(),
                    targetPart.getPartQuantity(),
                    "Deleted part: " + targetPart.getPartName()
            );

            partList.remove(targetPart);
            PartLoad.saveItems(filePath);
            return true;
        }

        return false;
    }
}