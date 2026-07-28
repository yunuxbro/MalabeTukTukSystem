package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.malabetuktuk.POS.managerClass;
import org.example.malabetuktuk.blueprints.Cartitems;
import org.example.malabetuktuk.loadFiles.PartLoad;

import java.util.ArrayList;
import java.util.List;

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
    @FXML private Label lblDiscountType;
    @FXML private Label lblDiscountAmount;
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

        btnClearCart.setOnAction(e -> handleClearCart());
        btnCheckout.setOnAction(e -> handleCheckout());
    }

    private void refreshCartDisplay() {
        cartObservableList.setAll(managerClass.getCart());
        tblCart.setItems(cartObservableList);
        calculateTotals();
    }

    private void calculateTotals() {
        List<Cartitems> cart = managerClass.getCart();

        double rawSubtotal = 0.0;
        for (Cartitems item : cart) {
            rawSubtotal += (item.getPart().getPartPrice() * item.getOrderedQuantity());
        }

        double grandTotal = managerClass.calculateSubtotal();
        double discountAmount = rawSubtotal - grandTotal;

        lblSubtotal.setText(String.format("Rs. %.2f", rawSubtotal));
        lblDiscountType.setText(discountAmount > 0 ? "Applied Savings / Discounts" : "None");
        lblDiscountAmount.setText(String.format("- Rs. %.2f", discountAmount));
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
                    PartLoad.saveItems("data/updated_data_legacy.txt");
                    refreshCartDisplay();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRemove);
            }
        });
    }

    private void handleClearCart() {
        List<Cartitems> itemsCopy = new ArrayList<>(managerClass.getCart());
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
        double grandTotal = managerClass.calculateSubtotal();
        boolean success = managerClass.processCheckout();

        if (success) {
            refreshCartDisplay();
            showNotification(Alert.AlertType.INFORMATION, "Transaction Receipt",
                    String.format("Transaction Completed Successfully!\nTotal Paid: Rs. %.2f via %s", grandTotal, payment));
        } else {
            showNotification(Alert.AlertType.ERROR, "Error", "Failed to process checkout transaction!");
        }
    }

    private void showNotification(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}