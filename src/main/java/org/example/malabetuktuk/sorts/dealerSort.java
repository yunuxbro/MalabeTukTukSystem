package org.example.malabetuktuk.sorts;

import org.example.malabetuktuk.blueprints.Dealer;
import java.util.ArrayList;

public class dealerSort {

    public static void bubbleSortByCity(ArrayList<Dealer> dealers) {
        int n = dealers.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String city1 = dealers.get(j).getDealerCity();
                String city2 = dealers.get(j + 1).getDealerCity();

                if (city1.compareToIgnoreCase(city2) > 0) {
                    Dealer temp = dealers.get(j);
                    dealers.set(j, dealers.get(j + 1));
                    dealers.set(j + 1, temp);
                }
            }
        }
    }
}