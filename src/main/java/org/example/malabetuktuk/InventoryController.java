package org.example.malabetuktuk;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.POS.InventoryFilter;
import org.example.malabetuktuk.POS.LowStock;
import org.example.malabetuktuk.sorts.itemSort;

import java.io.File;
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

    @FXML private VBox vboxCategoryContainer;

    @FXML
    public void initialize() {
        if (PartLoad.getInventoryList().isEmpty()) {
            PartLoad.loadItems("data/inventory_legacy.txt");
        }

        if (cmbCategory.getItems().isEmpty()) {
            cmbCategory.getItems().addAll("All", "ENGINE", "ELECTRICAL", "BODY", "BRAKE");
            cmbCategory.setValue("All");
        }

        refreshScreen();

        btnSearch.setOnAction(e -> applyFilter());
        txtThreshold.textProperty().addListener((obs, oldVal, newVal) -> updateLowStockAlerts());
    }

    private void refreshScreen() {
        itemSort.sortInventory();
        renderGridCards(PartLoad.getInventoryList());
        updateKPIs();
        updateLowStockAlerts();
    }

    private void renderGridCards(ArrayList<Part> parts) {
        vboxCategoryContainer.getChildren().clear();

        String currentCategory = "";
        FlowPane currentFlowPane = null;

        for (Part part : parts) {
            if (!part.getPartType().equalsIgnoreCase(currentCategory)) {
                currentCategory = part.getPartType();

                Label lblCategoryHeader = new Label("CATEGORY: " + currentCategory.toUpperCase());
                lblCategoryHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 0 5 0; -fx-text-fill: #333;");
                vboxCategoryContainer.getChildren().add(lblCategoryHeader);

                currentFlowPane = new FlowPane();
                currentFlowPane.setHgap(15);
                currentFlowPane.setVgap(15);
                vboxCategoryContainer.getChildren().add(currentFlowPane);
            }

            VBox card = createCard(part);
            if (currentFlowPane != null) {
                currentFlowPane.getChildren().add(card);
            }
        }
    }

    private VBox createCard(Part part) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(160);
        card.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #fafafa;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        String path = part.getImagePath();
        Image img = null;
        if (path != null && !path.equals("null") && !path.trim().isEmpty()) {
            File file = new File(path);
            if (!file.exists()) file = new File("data/" + path);
            if (!file.exists()) file = new File("data/images/" + path);

            if (file.exists()) {
                img = new Image(file.toURI().toString(), 100, 100, true, true);
            }
        }
        if (img == null) {
            img = new Image("https://via.placeholder.com/100?text=No+Image", 100, 100, true, true);
        }
        imageView.setImage(img);

        Label lblName = new Label(part.getPartName());
        lblName.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        lblName.setWrapText(true);

        Label lblId = new Label("ID: " + part.getPartId());
        lblId.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        Label lblQty = new Label("Qty: " + part.getPartQuantity());
        lblQty.setStyle("-fx-font-size: 12px;");

        Label lblPrice = new Label(String.format("Rs. %.2f", part.getPartPrice()));
        lblPrice.setStyle("-fx-font-weight: bold; -fx-text-fill: #2a72d0;");

        Button btnAddToCart = new Button("Add to Cart");
        btnAddToCart.setOnAction(e -> {
            if (part.getPartQuantity() > 0) {
                part.setPartQuantity(part.getPartQuantity() - 1);
                PartLoad.saveItems("data/updated_data_legacy.txt");
                refreshScreen();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Item is out of stock!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        card.getChildren().addAll(imageView, lblName, lblId, lblQty, lblPrice, btnAddToCart);
        return card;
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
            // Keep default limits on error
        }

        ArrayList<Part> filtered = InventoryFilter.filter(category, minPrice, maxPrice, keyword);
        renderGridCards(filtered);
    }
}