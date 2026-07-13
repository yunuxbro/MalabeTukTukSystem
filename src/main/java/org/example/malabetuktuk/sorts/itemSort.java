package org.example.malabetuktuk.sorts;

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;

import java.util.ArrayList;

public class itemSort {

    public static void sortInventory(){
        ArrayList<Part> list = PartLoad.getInventoryList();
        int n = list.size();

        for(int i = 0; i < n - 1; i++){
            for(int j = 0; j < n - i - 1; j++){
                Part temp = list.get(j);
                Part nextTemp = list.get(j+1);
                boolean status = false;
                int categorySort = temp.getPartType().compareTo(nextTemp.getPartType());
                if(categorySort > 0){
                    status = true;
                }
                else if(categorySort == 0){
                    int idSort = temp.getPartType().compareTo(nextTemp.getPartType());
                    if(idSort > 0){
                        status = true;
                    }
                }

                if(status){
                    Part element = temp;
                    list.set(j, nextTemp);
                    list.set(j+1, element);
                }
            }
        }

    }
}
