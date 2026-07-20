package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.POS.InventoryFilter;
import org.example.malabetuktuk.POS.LowStock;
import org.example.malabetuktuk.sorts.itemSort;

import java.util.ArrayList;

public class InventoryController {

    @FXML private Label lblTotalItems;
    @FXML private Label lblTotalValue;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtMinPrice;
    @FXML private TextField txtMaxPrice;
    @FXML private TextField txtKeyword;
    @FXML private Button btnSearch;
    @FXML private TextField txtThreshold;
    @FXML private Label lblLowStockItems;

    @FXML private TableView<Part> tblInventory;
    @FXML private TableColumn<Part, String> colId;
    @FXML private TableColumn<Part, String> colName;
    @FXML private TableColumn<Part, String> colCategory;
    @FXML private TableColumn<Part, String> colBrand;
    @FXML private TableColumn<Part, Double> colPrice;
    @FXML private TableColumn<Part, Integer> colQuantity;
    @FXML private TableColumn<Part, Void> colAction;

    private ObservableList<Part> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("partId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("partType"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("partBrand"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("partQuantity"));

        if (cmbCategory.getItems().isEmpty()) {
            cmbCategory.getItems().addAll("All", "ENGINE", "ELECTRICAL", "BODY", "BRAKE");
            cmbCategory.setValue("All");
        }

        setupActionColumn();

        refreshScreen();

        btnSearch.setOnAction(e -> applyFilter());
        txtThreshold.textProperty().addListener((obs, oldVal, newVal) -> updateLowStockAlerts());
    }

    private void refreshScreen() {
        itemSort.sortInventory();

        tableData.setAll(PartLoad.getInventoryList());
        tblInventory.setItems(tableData);

        updateKPIs();
        updateLowStockAlerts();
    }

    private void updateKPIs() {
        ArrayList<Part> list = PartLoad.getInventoryList();
        int totalQty = 0;
        double totalVal = 0.0;

        for (Part p : list) {
            totalQty += p.getPartQuantity();
            totalVal += (p.getPartPrice() * p.getPartQuantity());
        }

        lblTotalItems.setText(String.valueOf(totalQty));
        lblTotalValue.setText(String.format("Rs. %.2f", totalVal));
    }

    private void updateLowStockAlerts() {
        try {
            int barrier = Integer.parseInt(txtThreshold.getText().trim());
            ArrayList<Part> lowStock = LowStock.getLowStockParts(barrier);

            if (lowStock.isEmpty()) {
                lblLowStockItems.setText("None");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Part p : lowStock) {
                    sb.append(p.getPartName()).append(" (Qty: ").append(p.getPartQuantity()).append("), ");
                }
                lblLowStockItems.setText(sb.substring(0, sb.length() - 2));
            }
        } catch (NumberFormatException e) {
            lblLowStockItems.setText("Invalid threshold number");
        }
    }

    private void applyFilter() {
        String category = cmbCategory.getValue();
        String keyword = txtKeyword.getText();

        double minPrice = 0.0;
        double maxPrice = Double.MAX_VALUE;

        try {
            if (!txtMinPrice.getText().trim().isEmpty()) {
                minPrice = Double.parseDouble(txtMinPrice.getText().trim());
            }
            if (!txtMaxPrice.getText().trim().isEmpty()) {
                maxPrice = Double.parseDouble(txtMaxPrice.getText().trim());
            }
        } catch (NumberFormatException e) {
        }

        ArrayList<Part> filtered = InventoryFilter.filter(category, minPrice, maxPrice, keyword);
        tableData.setAll(filtered);
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnAddToCart = new Button("Add to Cart");

            {
                btnAddToCart.setOnAction(e -> {
                    Part part = getTableView().getItems().get(getIndex());
                    if (part.getPartQuantity() > 0) {
                        part.setPartQuantity(part.getPartQuantity() - 1);

                        PartLoad.saveItems("data/updated_data_legacy.txt");

                        tblInventory.refresh();
                        updateKPIs();
                        updateLowStockAlerts();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Item is out of stock!", ButtonType.OK);
                        alert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnAddToCart);
                }
            }
        });
    }
}