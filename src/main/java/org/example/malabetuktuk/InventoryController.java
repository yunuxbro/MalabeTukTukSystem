package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.POS.InventoryFilter;
import org.example.malabetuktuk.POS.LowStock;
import org.example.malabetuktuk.POS.managerClass;
import org.example.malabetuktuk.sorts.itemSort;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

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
        renderCategoryTables(PartLoad.getInventoryList());
        updateKPIs();
        updateLowStockAlerts();
    }

    private void renderCategoryTables(ArrayList<Part> parts) {
        vboxCategoryContainer.getChildren().clear();

        // Map to group parts by Category
        Map<String, ArrayList<Part>> groupedParts = new LinkedHashMap<>();

        for (Part part : parts) {
            String cat = part.getPartType() == null ? "UNCATEGORIZED" : part.getPartType().toUpperCase();
            groupedParts.putIfAbsent(cat, new ArrayList<>());
            groupedParts.get(cat).add(part);
        }

        // Render each category with a section header label and a TableView
        for (Map.Entry<String, ArrayList<Part>> entry : groupedParts.entrySet()) {
            String categoryName = entry.getKey();
            ArrayList<Part> categoryList = entry.getValue();

            // Sort by Part ID within the category
            categoryList.sort(Comparator.comparing(Part::getPartId, String.CASE_INSENSITIVE_ORDER));

            // Topic Header Label
            Label lblCategoryHeader = new Label("CATEGORY: " + categoryName);
            lblCategoryHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 0 5 0; -fx-text-fill: #2c3e50;");
            vboxCategoryContainer.getChildren().add(lblCategoryHeader);

            // Category TableView
            TableView<Part> table = createCategoryTable(categoryList);
            vboxCategoryContainer.getChildren().add(table);
        }
    }

    private TableView<Part> createCategoryTable(ArrayList<Part> parts) {
        TableView<Part> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        double rowHeight = 60.0;
        double headerHeight = 35.0;
        table.setPrefHeight((parts.size() * rowHeight) + headerHeight);

        // Column 1: Image Preview
        TableColumn<Part, String> colImage = new TableColumn<>("Preview");
        colImage.setPrefWidth(80);
        colImage.setStyle("-fx-alignment: CENTER;");
        colImage.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        colImage.setCellFactory(param -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Image img = loadPartImage(imagePath);
                    imageView.setImage(img);
                    setGraphic(imageView);
                }
            }
        });

        TableColumn<Part, String> colId = new TableColumn<>("Part ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("partId"));
        colId.setPrefWidth(90);

        TableColumn<Part, String> colName = new TableColumn<>("Part Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        colName.setPrefWidth(180);

        TableColumn<Part, String> colBrand = new TableColumn<>("Brand");
        colBrand.setCellValueFactory(new PropertyValueFactory<>("partBrand"));
        colBrand.setPrefWidth(100);

        TableColumn<Part, Double> colPrice = new TableColumn<>("Price (Rs.)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));
        colPrice.setPrefWidth(100);

        TableColumn<Part, Integer> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(new PropertyValueFactory<>("partQuantity"));
        colQty.setPrefWidth(70);

        TableColumn<Part, Void> colAction = new TableColumn<>("Actions");
        colAction.setPrefWidth(160);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnAdd = new Button("Add to Cart");
            private final Button btnEdit = new Button("View/Edit");
            private final HBox container = new HBox(5, btnAdd, btnEdit);

            {
                container.setAlignment(Pos.CENTER);
                btnAdd.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 11px;");
                btnEdit.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 11px;");

                btnAdd.setOnAction(e -> {
                    Part part = getTableView().getItems().get(getIndex());
                    if (part.getPartQuantity() > 0) {
                        part.setPartQuantity(part.getPartQuantity() - 1);
                        managerClass.addToCart(part);
                        PartLoad.saveItems("data/updated_data_legacy.txt");
                        refreshScreen();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Item is out of stock!", ButtonType.OK);
                        alert.showAndWait();
                    }
                });

                btnEdit.setOnAction(e -> {
                    Part part = getTableView().getItems().get(getIndex());
                    openEditWindow(part);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        table.getColumns().addAll(colImage, colId, colName, colBrand, colPrice, colQty, colAction);

        table.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Part rowData = row.getItem();
                    openEditWindow(rowData);
                }
            });
            return row;
        });

        ObservableList<Part> observableParts = FXCollections.observableArrayList(parts);
        table.setItems(observableParts);

        return table;
    }

    private void openEditWindow(Part part) {
        Stage editStage = new Stage();
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.setTitle("Product Details & Edit - " + part.getPartId());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        // Preview Image View
        ImageView imgPreview = new ImageView(loadPartImage(part.getImagePath()));
        imgPreview.setFitWidth(120);
        imgPreview.setFitHeight(120);
        imgPreview.setPreserveRatio(true);

        Label lblTitle = new Label("Product Specification");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtId = new TextField(part.getPartId());
        txtId.setDisable(true); // ID is read-only primary key

        TextField txtName = new TextField(part.getPartName());
        TextField txtCategory = new TextField(part.getPartType());
        TextField txtBrand = new TextField(part.getPartBrand());
        TextField txtPrice = new TextField(String.valueOf(part.getPartPrice()));
        TextField txtQty = new TextField(String.valueOf(part.getPartQuantity()));
        TextField txtImagePath = new TextField(part.getImagePath() == null ? "" : part.getImagePath());

        Button btnBrowseImage = new Button("Browse Image...");
        btnBrowseImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Product Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
            );
            File selectedFile = fileChooser.showOpenDialog(editStage);
            if (selectedFile != null) {
                txtImagePath.setText(selectedFile.getAbsolutePath());
                imgPreview.setImage(new Image(selectedFile.toURI().toString(), 120, 120, true, true));
            }
        });

        HBox imagePathBox = new HBox(5, txtImagePath, btnBrowseImage);

        grid.add(lblTitle, 0, 0, 2, 1);
        grid.add(new Label("Image Preview:"), 0, 1);
        grid.add(imgPreview, 1, 1);

        grid.add(new Label("Part ID:"), 0, 2);
        grid.add(txtId, 1, 2);

        grid.add(new Label("Part Name:"), 0, 3);
        grid.add(txtName, 1, 3);

        grid.add(new Label("Category:"), 0, 4);
        grid.add(txtCategory, 1, 4);

        grid.add(new Label("Brand:"), 0, 5);
        grid.add(txtBrand, 1, 5);

        grid.add(new Label("Price (Rs.):"), 0, 6);
        grid.add(txtPrice, 1, 6);

        grid.add(new Label("Quantity:"), 0, 7);
        grid.add(txtQty, 1, 7);

        grid.add(new Label("Image File Path:"), 0, 8);
        grid.add(imagePathBox, 1, 8);

        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(e -> editStage.close());

        btnSave.setOnAction(e -> {
            try {
                part.setPartName(txtName.getText().trim());
                part.setPartType(txtCategory.getText().trim().toUpperCase());
                part.setPartBrand(txtBrand.getText().trim());
                part.setPartPrice(Double.parseDouble(txtPrice.getText().trim()));
                part.setPartQuantity(Integer.parseInt(txtQty.getText().trim()));
                part.setImagePath(txtImagePath.getText().trim());

                // Persist updated records to file storage
                PartLoad.saveItems("data/updated_data_legacy.txt");

                refreshScreen();
                editStage.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Product details updated successfully!", ButtonType.OK);
                alert.showAndWait();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price or quantity value! Please check input.", ButtonType.OK);
                alert.showAndWait();
            }
        });

        HBox buttonBox = new HBox(10, btnSave, btnCancel);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttonBox, 1, 9);

        Scene scene = new Scene(grid, 480, 560);
        editStage.setScene(scene);
        editStage.showAndWait();
    }

    private Image loadPartImage(String path) {
        if (path != null && !path.equals("null") && !path.trim().isEmpty()) {
            File file = new File(path);
            if (!file.exists()) file = new File("data/" + path);
            if (!file.exists()) file = new File("data/images/" + path);

            if (file.exists()) {
                return new Image(file.toURI().toString(), 50, 50, true, true);
            }
        }
        return new Image("https://via.placeholder.com/50?text=No+Image", 50, 50, true, true);
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
            // Keep default boundaries
        }

        ArrayList<Part> filtered = InventoryFilter.filter(category, minPrice, maxPrice, keyword);
        renderCategoryTables(filtered);
    }
}