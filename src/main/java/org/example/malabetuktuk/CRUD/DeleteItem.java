package org.example.malabetuktuk.CRUD;

import org.example.malabetuktuk.POS.managerClass;
import org.example.malabetuktuk.loadFiles.PartLoad;

public class DeleteItem {
    public static boolean executeDelete(String partId, String filePath){
        boolean status = false;
        for (int i = 0; i < PartLoad.getInventoryList().size(); i++) {
            if (PartLoad.getInventoryList().get(i).getPartId().equalsIgnoreCase(partId)) {
                PartLoad.getInventoryList().remove(i);
                managerClass.logAuditAction("DELETE_PART", partId, p.getPartQuantity(), "Deleted part: " + p.getPartName());

                status = true;
                break;
            }
        }
        if (status) {
            PartLoad.saveItems(filePath);
            return true;
        }
        return false;
    }
}