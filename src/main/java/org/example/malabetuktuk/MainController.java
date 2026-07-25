package org.example.malabetuktuk;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private Button btnInventory;
    @FXML private Button btnCart;
    @FXML private Button btnDealers;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        loadView("inventory.fxml");

        btnInventory.setOnAction(e -> loadView("inventory.fxml"));
        btnCart.setOnAction(e -> loadView("cart.fxml"));
        btnDealers.setOnAction(e -> loadView("dealers.fxml"));
    }

    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}