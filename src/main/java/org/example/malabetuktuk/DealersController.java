package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.malabetuktuk.POS.RandomDealers;
import org.example.malabetuktuk.blueprints.Dealer;
import org.example.malabetuktuk.loadFiles.DealerLoad;

import java.util.ArrayList;

public class DealersController {

    @FXML private TableView<Dealer> allDealersTable;
    @FXML private TableColumn<Dealer, String> colAllId;
    @FXML private TableColumn<Dealer, String> colAllName;
    @FXML private TableColumn<Dealer, Integer> colAllNum;
    @FXML private TableColumn<Dealer, String> colAllCity;

    @FXML private TableView<Dealer> randomDealersTable;
    @FXML private TableColumn<Dealer, String> colRandomId;
    @FXML private TableColumn<Dealer, String> colRandomName;
    @FXML private TableColumn<Dealer, Integer> colRandomNum;
    @FXML private TableColumn<Dealer, String> colRandomCity;

    @FXML private Button btnSelectRandom;

    @FXML
    public void initialize() {
        colAllId.setCellValueFactory(new PropertyValueFactory<>("dealerId"));
        colAllName.setCellValueFactory(new PropertyValueFactory<>("dealerName"));
        colAllNum.setCellValueFactory(new PropertyValueFactory<>("dealerNum"));
        colAllCity.setCellValueFactory(new PropertyValueFactory<>("dealerCity"));

        colRandomId.setCellValueFactory(new PropertyValueFactory<>("dealerId"));
        colRandomName.setCellValueFactory(new PropertyValueFactory<>("dealerName"));
        colRandomNum.setCellValueFactory(new PropertyValueFactory<>("dealerNum"));
        colRandomCity.setCellValueFactory(new PropertyValueFactory<>("dealerCity"));

        loadAllDealersTable();
    }

    private void loadAllDealersTable() {
        ArrayList<Dealer> allDealers = DealerLoad.getDealerList();
        ObservableList<Dealer> observableList = FXCollections.observableArrayList(allDealers);
        allDealersTable.setItems(observableList);
    }

    @FXML
    private void handleSelectRandomDealers() {
        ArrayList<Dealer> selectedSorted = RandomDealers.selectDealers();
        ObservableList<Dealer> observableSelected = FXCollections.observableArrayList(selectedSorted);
        randomDealersTable.setItems(observableSelected);
    }
}