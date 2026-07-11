package org.example.malabetuktuk.loadFiles;

import org.example.malabetuktuk.blueprints.Dealer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DealerLoad {
    private static ArrayList<Dealer> dealerList = new ArrayList<Dealer>();

    public static void loadDealers(String fileName) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.trim().split("[,;|]");

                if (parts.length >= 4) {
                    String id = parts[0].trim();

                    String name = parts[1].trim();
                    if (name.isEmpty()) {
                        name = "null";
                    }

                    // Handle empty or unparseable integer number
                    String numStr = parts[2].trim();
                    int num = 0;
                    if (!numStr.isEmpty()) {
                        num = Integer.parseInt(numStr);
                    }

                    String city = parts[3].trim();
                    if (city.isEmpty()) {
                        city = "null";
                    }

                    Dealer newDealer = new Dealer(id, name, num, city);
                    dealerList.add(newDealer);
                }
            }
            System.out.println("Successfully loaded " + dealerList.size() + " dealer objects into memory!");

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static ArrayList<Dealer> getDealerList() {
        return dealerList;
    }

    public static void main(String[] args) {
        loadDealers("data/dealers_legacy.txt");

    }
}