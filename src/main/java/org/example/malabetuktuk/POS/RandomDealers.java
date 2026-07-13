package org.example.malabetuktuk.POS;
import org.example.malabetuktuk.blueprints.Dealer;
import org.example.malabetuktuk.loadFiles.DealerLoad;

import java.util.ArrayList;
import java.util.Random;

public class RandomDealers {
    public static ArrayList<Dealer> selectDealers(){

        ArrayList<Dealer> alldealers = DealerLoad.getDealerList();
        ArrayList<Dealer> selectedDealers = new ArrayList<>();
        Random rand = new Random();

        if(alldealers.size()<=4){
            return new ArrayList<>(alldealers);
        }

        while(selectedDealers.size()<4){
            int index = rand.nextInt(alldealers.size());
            Dealer newdealer = alldealers.get(index);

            if(!selectedDealers.contains(newdealer)){
                selectedDealers.add(newdealer);
            }
        }
        return selectedDealers;
    }
}
