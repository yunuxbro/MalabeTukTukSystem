package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.malabetuktuk.blueprints.Cartitems;
import org.example.malabetuktuk.POS.managerClass;
import org.example.malabetuktuk.loadFiles.PartLoad;

public class CartController {

    @FXML private TableView<Cartitems> tblCart;
    @FXML private TableColumn<Cartitems, String> colPartId;
    @FXML private TableColumn<Cartitems, String> colPartName;
    @FXML private TableColumn<Cartitems, Double> colPrice;
    @FXML private TableColumn<Cartitems, Integer> colQuantity;
    @FXML private TableColumn<Cartitems, Double> colTotal;
    @FXML private TableColumn<Cartitems, Void> colAction;

    @FXML private Button btnClearCart;
    @FXML private Label lblSubtotal;
    @FXML private TextField txtDiscount;
    @FXML private Label lblGrandTotal;
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private Button btnCheckout;

    private final ObservableList<Cartitems> cartObservableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colPartId.setCellValueFactory(new PropertyValueFactory<>("partId"));
        colPartName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("orderedQuantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        if (cmbPaymentMethod.getItems().isEmpty()) {
            cmbPaymentMethod.getItems().addAll("Cash", "Credit/Debit Card", "Bank Transfer", "Mobile Pay");
            cmbPaymentMethod.setValue("Cash");
        }

        setupActionColumn();
        refreshCartDisplay();

        txtDiscount.textProperty().addListener((obs, oldVal, newVal) -> calculateTotals());

        btnClearCart.setOnAction(e -> handleClearCart());
        btnCheckout.setOnAction(e -> handleCheckout());
    }

    private void refreshCartDisplay() {
        cartObservableList.setAll(managerClass.getCart());
        tblCart.setItems(cartObservableList);
        calculateTotals();
    }

    private void calculateTotals() {
        double subtotal = managerClass.calculateSubtotal();
        double discountPercent = 0.0;

        try {
            if (!txtDiscount.getText().trim().isEmpty()) {
                discountPercent = Double.parseDouble(txtDiscount.getText().trim());
                if (discountPercent < 0 || discountPercent > 100) {
                    discountPercent = 0;
                }
            }
        } catch (NumberFormatException e) {
        }

        double discountAmount = subtotal * (discountPercent / 100.0);
        double grandTotal = subtotal - discountAmount;

        lblSubtotal.setText(String.format("Rs. %.2f", subtotal));
        lblGrandTotal.setText(String.format("Rs. %.2f", grandTotal));
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnRemove = new Button("Remove");
            {
                btnRemove.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000;");
                btnRemove.setOnAction(e -> {
                    Cartitems item = getTableView().getItems().get(getIndex());
                    managerClass.removeFromCart(item);
                    PartLoad.saveItems("data/updated_data_legacy.txt"); // Persist updated inventory levels
                    refreshCartDisplay();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRemove);
                }
            }
        });
    }

    private void handleClearCart() {
        var itemsCopy = new java.util.ArrayList<>(managerClass.getCart());
        for (Cartitems item : itemsCopy) {
            managerClass.removeFromCart(item);
        }
        PartLoad.saveItems("data/updated_data_legacy.txt");
        refreshCartDisplay();
    }

    private void handleCheckout() {
        if (!managerClass.cartvalidation(managerClass.getCart())) {
            showNotification(Alert.AlertType.WARNING, "Checkout Error", "Your shopping cart is empty!");
            return;
        }

        String payment = cmbPaymentMethod.getValue();
        double grandTotal = managerClass.calculateSubtotal() * (1 - (Double.parseDouble("0" + txtDiscount.getText()) / 100.0));

        PartLoad.saveItems("data/updated_data_legacy.txt");

        managerClass.clearCart();
        refreshCartDisplay();

        showNotification(Alert.AlertType.INFORMATION, "Success",
                String.format("Transaction Completed successfully!\nTotal paid: Rs. %.2f via %s", grandTotal, payment));
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}