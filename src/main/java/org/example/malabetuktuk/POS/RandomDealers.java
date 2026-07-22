package org.example.malabetuktuk.POS;
import org.example.malabetuktuk.blueprints.Dealer;
import org.example.malabetuktuk.loadFiles.DealerLoad;

import java.util.ArrayList;
import java.util.Random;

public class RandomDealers {
    public static ArrayList<Dealer> selectDealers() {
        ArrayList<Dealer> alldealers = DealerLoad.getDealerList();
        ArrayList<Dealer> selectedDealers = new ArrayList<>();
        Random rand = new Random();

        if (alldealers.size() <= 4) {
            selectedDealers = new ArrayList<>(alldealers);
        } else {
            while (selectedDealers.size() < 4) {
                int index = rand.nextInt(alldealers.size());
                Dealer newdealer = alldealers.get(index);
                if (!selectedDealers.contains(newdealer)) {
                    selectedDealers.add(newdealer);
                }
            }
        }

        int n = selectedDealers.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                String loc1 = selectedDealers.get(j).getDealerCity();
                String loc2 = selectedDealers.get(j + 1).getDealerCity();
                if (loc1.compareToIgnoreCase(loc2) > 0) {
                    Dealer temp = selectedDealers.get(j);
                    selectedDealers.set(j, selectedDealers.get(j + 1));
                    selectedDealers.set(j + 1, temp);
                }
            }
        }

        return selectedDealers;
    }
}
