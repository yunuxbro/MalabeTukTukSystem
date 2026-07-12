package org.example.malabetuktuk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class HelloController {

    // --- FXML Layout Component Bindings ---
    @FXML private Button btnNavDealers;
    @FXML private Button btnNavInventory;
    @FXML private Button btnReload;
    @FXML private Button btnCheckLowStock;
    @FXML private Label lblLoadedStatus;
    @FXML private Label lblTotalParts;
    @FXML private Label lblTotalValue;
    @FXML private TextField txtThreshold;

    // Tables
    @FXML private TableView<?> tblInventory;
    @FXML private TableColumn<?, ?> colId;
    @FXML private TableColumn<?, ?> colProduct;
    @FXML private TableColumn<?, ?> colBrand;
    @FXML private TableColumn<?, ?> colPrice;
    @FXML private TableColumn<?, ?> colStock;
    @FXML private TableColumn<?, ?> colCategory;
    @FXML private TableColumn<?, ?> colDate;
    @FXML private TableColumn<?, ?> colImage;

    @FXML private TableView<?> tblLowStock;
    @FXML private TableColumn<?, ?> colLowId;
    @FXML private TableColumn<?, ?> colLowProduct;
    @FXML private TableColumn<?, ?> colLowBrand;
    @FXML private TableColumn<?, ?> colLowPrice;
    @FXML private TableColumn<?, ?> colLowStock;
    @FXML private TableColumn<?, ?> colLowCategory;
    @FXML private TableColumn<?, ?> colLowDate;
    @FXML private TableColumn<?, ?> colLowImage;

    // --- FXML Linked Click Action Stubs ---
    @FXML
    void handleReloadAll(ActionEvent event) {
        System.out.println("[Wireframe Action] Reload All triggered.");
    }

    @FXML
    void handleCheckLowStock(ActionEvent event) {
        System.out.println("[Wireframe Action] Run Analysis triggered. Threshold: " + txtThreshold.getText());
    }

    @FXML
    public void initialize() {
        System.out.println("Low-fidelity wireframe controller connected successfully.");
    }
}