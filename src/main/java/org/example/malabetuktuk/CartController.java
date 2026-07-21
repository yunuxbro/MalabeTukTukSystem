package org.example.malabetuktuk;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CartController {

    @FXML private TableView<?> tblCart;
    @FXML private TableColumn<?, ?> colPartId;
    @FXML private TableColumn<?, ?> colPartName;
    @FXML private TableColumn<?, ?> colPrice;
    @FXML private TableColumn<?, ?> colQuantity;
    @FXML private TableColumn<?, ?> colTotal;
    @FXML private TableColumn<?, ?> colAction;

    @FXML private Button btnClearCart;
    @FXML private Label lblSubtotal;
    @FXML private TextField txtDiscount;
    @FXML private Label lblGrandTotal;
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private Button btnCheckout;

    @FXML
    public void initialize() {
        if (cmbPaymentMethod != null) {
            cmbPaymentMethod.getItems().addAll("Cash", "Card", "Online Transfer");
            cmbPaymentMethod.setValue("Cash");
        }
    }
}