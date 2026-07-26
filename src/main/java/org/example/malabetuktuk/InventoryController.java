package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.POS.InventoryFilter;
import org.example.malabetuktuk.POS.LowStock;
import org.example.malabetuktuk.POS.managerClass;
import org.example.malabetuktuk.sorts.itemSort;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class InventoryController {

    @FXML private Label lblTotalItems;
    @FXML private Label lblTotalValue;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtMinPrice;
    @FXML private TextField txtMaxPrice;
    @FXML private TextField txtKeyword;
    @FXML private Button btnSearch;
    @FXML private Button btnAddItem;
    @FXML private Label lblLowStockItems;

    @FXML private TableView<Part> tblInventory;
    @FXML private TableColumn<Part, Void> colImg;
    @FXML private TableColumn<Part, String> colId;
    @FXML private TableColumn<Part, String> colName;
    @FXML private TableColumn<Part, String> colCategory;
    @FXML private TableColumn<Part, String> colBrand;
    @FXML private TableColumn<Part, Double> colPrice;
    @FXML private TableColumn<Part, Integer> colQty;
    @FXML private TableColumn<Part, Void> colAction;

    private ObservableList<Part> inventoryData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (PartLoad.getInventoryList().isEmpty()) {
            PartLoad.loadItems("data/inventory_legacy.txt");
        }

        if (cmbCategory.getItems().isEmpty()) {
            cmbCategory.getItems().addAll("All", "ENGINE", "ELECTRICAL", "BODY", "BRAKE");
            cmbCategory.setValue("All");
        }

        colId.setCellValueFactory(new PropertyValueFactory<>("partId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("partType"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("partBrand"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("partPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("partQuantity"));

        colImg.setCellFactory(new Callback<>() {
            public TableCell<Part, Void> call(TableColumn<Part, Void> param) {
                return new TableCell<>() {
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Part part = getTableView().getItems().get(getIndex());
                            String path = part.getImagePath();
                            ImageView imgView = new ImageView();
                            imgView.setFitWidth(45);
                            imgView.setFitHeight(45);
                            imgView.setPreserveRatio(true);

                            Image img = null;
                            if (path != null && !path.equalsIgnoreCase("null") && !path.trim().isEmpty()) {
                                File file = new File(path);
                                if (!file.exists()) file = new File("data/" + path);
                                if (!file.exists()) file = new File("data/images/" + path);
                                if (file.exists()) {
                                    img = new Image(file.toURI().toString(), 45, 45, true, true);
                                }
                            }

                            if (img != null) {
                                imgView.setImage(img);
                                setGraphic(imgView);
                            } else {
                                Label noImgLabel = new Label("No Image");
                                noImgLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                                setGraphic(noImgLabel);
                            }
                        }
                    }
                };
            }
        });

        colAction.setCellFactory(new Callback<>() {
            public TableCell<Part, Void> call(TableColumn<Part, Void> param) {
                return new TableCell<>() {
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Part part = getTableView().getItems().get(getIndex());
                            int currentStock = part.getPartQuantity();

                            Button btnEdit = new Button("View/Edit");
                            Button btnAdd = new Button("Add to Cart");
                            Spinner<Integer> qtySpinner = new Spinner<>(1, currentStock > 0 ? currentStock : 1, 1);
                            qtySpinner.setPrefWidth(65);
                            qtySpinner.setEditable(true);

                            if (currentStock <= 0) {
                                qtySpinner.setDisable(true);
                                btnAdd.setDisable(true);
                            }

                            btnEdit.setOnAction(event -> openEditWindow(part));

                            btnAdd.setOnAction(event -> {
                                int requestedQty = qtySpinner.getValue();
                                if (part.getPartQuantity() >= requestedQty && requestedQty > 0) {
                                    part.setPartQuantity(part.getPartQuantity() - requestedQty);
                                    for (int i = 0; i < requestedQty; i++) {
                                        managerClass.addToCart(part);
                                    }
                                    PartLoad.saveItems("data/updated_data_legacy.txt");
                                    refreshScreen();
                                }
                            });

                            HBox box = new HBox(5, qtySpinner, btnAdd, btnEdit);
                            setGraphic(box);
                        }
                    }
                };
            }
        });

        tblInventory.setRowFactory(param -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openEditWindow(row.getItem());
                }
            });
            return row;
        });

        refreshScreen();

        btnSearch.setOnAction(event -> applyFilter());
        btnAddItem.setOnAction(event -> openAddWindow());
    }

    private void refreshScreen() {
        itemSort.sortInventory();
        inventoryData.setAll(PartLoad.getInventoryList());
        tblInventory.setItems(inventoryData);

        updateKPIs();
        updateLowStockAlerts();
    }

    private void openAddWindow() {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add New Inventory Item");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField txtId = new TextField();
        TextField txtName = new TextField();
        TextField txtBrand = new TextField();
        TextField txtPrice = new TextField();
        TextField txtQty = new TextField();
        TextField txtType = new TextField();
        TextField txtThresholdInput = new TextField("5");

        String currentFormattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
        TextField txtDate = new TextField(currentFormattedDate);
        TextField txtImgPath = new TextField("null");

        Label lblError = new Label();
        lblError.setTextFill(Color.RED);

        Button btnBrowse = new Button("Browse...");
        btnBrowse.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Product Image");
            File selectedFile = chooser.showOpenDialog(popupStage);
            if (selectedFile != null) {
                txtImgPath.setText(selectedFile.getName());
            }
        });

        grid.add(new Label("Part ID (*):"), 0, 0);
        grid.add(txtId, 1, 0);

        grid.add(new Label("Part Name (*):"), 0, 1);
        grid.add(txtName, 1, 1);

        grid.add(new Label("Brand:"), 0, 2);
        grid.add(txtBrand, 1, 2);

        grid.add(new Label("Price (Rs.) (*):"), 0, 3);
        grid.add(txtPrice, 1, 3);

        grid.add(new Label("Stock Quantity (*):"), 0, 4);
        grid.add(txtQty, 1, 4);

        grid.add(new Label("Category Type (*):"), 0, 5);
        grid.add(txtType, 1, 5);

        grid.add(new Label("Low Stock Threshold:"), 0, 6);
        grid.add(txtThresholdInput, 1, 6);

        grid.add(new Label("Date Added:"), 0, 7);
        grid.add(txtDate, 1, 7);

        grid.add(new Label("Image Path:"), 0, 8);
        HBox imgBox = new HBox(5, txtImgPath, btnBrowse);
        grid.add(imgBox, 1, 8);

        grid.add(lblError, 0, 9, 2, 1);

        Button btnSubmit = new Button("Add Item");
        btnSubmit.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSubmit.setOnAction(event -> {
            String partId = txtId.getText().trim();
            String name = txtName.getText().trim();
            String brand = txtBrand.getText().trim();
            if (brand.isEmpty()) brand = "Unknown";

            String type = txtType.getText().trim().toUpperCase();
            String date = PartLoad.normalizeDate(txtDate.getText().trim());
            String imgPath = txtImgPath.getText().trim();
            if (imgPath.isEmpty()) imgPath = "null";

            if (partId.isEmpty() || name.isEmpty() || type.isEmpty() || txtPrice.getText().trim().isEmpty() || txtQty.getText().trim().isEmpty()) {
                lblError.setText("Please fill in all mandatory fields (*)");
                return;
            }

            for (Part p : PartLoad.getInventoryList()) {
                if (p.getPartId().equalsIgnoreCase(partId)) {
                    lblError.setText("Part ID already exists!");
                    return;
                }
            }

            try {
                double price = Double.parseDouble(txtPrice.getText().trim());
                int qty = Integer.parseInt(txtQty.getText().trim());
                int threshold = Integer.parseInt(txtThresholdInput.getText().trim());

                Part newPart = new Part(partId, name, brand, price, qty, type, date, imgPath, threshold);
                PartLoad.getInventoryList().add(newPart);
                PartLoad.saveItems("data/updated_data_legacy.txt");

                managerClass.logAuditAction("ADD_PART", partId, qty, "Added new part: " + name);

                refreshScreen();
                popupStage.close();
            } catch (NumberFormatException ex) {
                lblError.setText("Invalid price, quantity, or threshold number!");
            }
        });

        grid.add(btnSubmit, 1, 10);

        Scene scene = new Scene(grid);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void openEditWindow(final Part part) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Product Details - " + part.getPartName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField txtImgPath = new TextField(part.getImagePath());
        Button btnBrowse = new Button("Browse Image...");
        btnBrowse.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Product Image");
            File selectedFile = chooser.showOpenDialog(popupStage);
            if (selectedFile != null) {
                txtImgPath.setText(selectedFile.getName());
            }
        });

        TextField txtName = new TextField(part.getPartName());
        TextField txtType = new TextField(part.getPartType());
        TextField txtBrand = new TextField(part.getPartBrand());
        TextField txtPrice = new TextField(String.valueOf(part.getPartPrice()));
        TextField txtQty = new TextField(String.valueOf(part.getPartQuantity()));
        TextField txtThresholdInput = new TextField(String.valueOf(part.getLowStockThreshold()));
        TextField txtDate = new TextField(part.getAddedDate());

        Label lblError = new Label();
        lblError.setTextFill(Color.RED);

        grid.add(new Label("Image Path/Name:"), 0, 0);
        HBox imgBox = new HBox(5, txtImgPath, btnBrowse);
        grid.add(imgBox, 1, 0);

        grid.add(new Label("Part ID:"), 0, 1);
        grid.add(new Label(part.getPartId()), 1, 1);

        grid.add(new Label("Part Name:"), 0, 2);
        grid.add(txtName, 1, 2);

        grid.add(new Label("Category Type:"), 0, 3);
        grid.add(txtType, 1, 3);

        grid.add(new Label("Brand:"), 0, 4);
        grid.add(txtBrand, 1, 4);

        grid.add(new Label("Price (Rs.):"), 0, 5);
        grid.add(txtPrice, 1, 5);

        grid.add(new Label("Stock Quantity:"), 0, 6);
        grid.add(txtQty, 1, 6);

        grid.add(new Label("Low Stock Threshold:"), 0, 7);
        grid.add(txtThresholdInput, 1, 7);

        grid.add(new Label("Date Added:"), 0, 8);
        grid.add(txtDate, 1, 8);

        grid.add(lblError, 0, 9, 2, 1);

        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(event -> {
            try {
                String newName = txtName.getText().trim();
                String newType = txtType.getText().trim().toUpperCase();
                String brandInput = txtBrand.getText().trim();
                String newBrand = brandInput.isEmpty() ? "Unknown" : brandInput;
                double newPrice = Double.parseDouble(txtPrice.getText().trim());
                int newQty = Integer.parseInt(txtQty.getText().trim());
                int newThreshold = Integer.parseInt(txtThresholdInput.getText().trim());
                String newDate = PartLoad.normalizeDate(txtDate.getText().trim());
                String imgInput = txtImgPath.getText().trim();
                String newImg = imgInput.isEmpty() ? "null" : imgInput;

                StringBuilder changeLog = new StringBuilder();

                if (!part.getPartName().equalsIgnoreCase(newName)) {
                    changeLog.append("Name: ").append(part.getPartName()).append(" -> ").append(newName).append(" | ");
                }
                if (!part.getPartBrand().equalsIgnoreCase(newBrand)) {
                    changeLog.append("Brand: ").append(part.getPartBrand()).append(" -> ").append(newBrand).append(" | ");
                }
                if (!part.getPartType().equalsIgnoreCase(newType)) {
                    changeLog.append("Category: ").append(part.getPartType()).append(" -> ").append(newType).append(" | ");
                }
                if (part.getPartPrice() != newPrice) {
                    changeLog.append("Price: Rs.").append(part.getPartPrice()).append(" -> Rs.").append(newPrice).append(" | ");
                }
                if (part.getPartQuantity() != newQty) {
                    changeLog.append("Qty: ").append(part.getPartQuantity()).append(" -> ").append(newQty).append(" | ");
                }
                if (part.getLowStockThreshold() != newThreshold) {
                    changeLog.append("Threshold: ").append(part.getLowStockThreshold()).append(" -> ").append(newThreshold).append(" | ");
                }

                String auditDetails = changeLog.length() > 0
                        ? changeLog.substring(0, changeLog.length() - 3)
                        : "No fields modified";

                part.setPartName(newName);
                part.setPartType(newType);
                part.setPartBrand(newBrand);
                part.setPartPrice(newPrice);
                part.setPartQuantity(newQty);
                part.setLowStockThreshold(newThreshold);
                part.setAddedDate(newDate);
                part.setImagePath(newImg);

                PartLoad.saveItems("data/updated_data_legacy.txt");

                managerClass.logAuditAction("UPDATE_PART", part.getPartId(), part.getPartQuantity(), auditDetails);

                refreshScreen();
                popupStage.close();
            } catch (NumberFormatException ex) {
                lblError.setText("Invalid price, quantity, or threshold number!");
            }
        });

        Button btnDelete = new Button("Delete Part");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        btnDelete.setOnAction(event -> {
            PartLoad.getInventoryList().remove(part);
            PartLoad.saveItems("data/updated_data_legacy.txt");

            managerClass.logAuditAction("DELETE_PART", part.getPartId(), part.getPartQuantity(), "Deleted part: " + part.getPartName());

            refreshScreen();
            popupStage.close();
        });

        HBox actionBox = new HBox(10, btnSave, btnDelete);
        grid.add(actionBox, 1, 10);

        Scene scene = new Scene(grid);
        popupStage.setScene(scene);
        popupStage.showAndWait();
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
        ArrayList<Part> lowStockList = LowStock.getLowStockParts();

        if (lowStockList.isEmpty()) {
            lblLowStockItems.setText("All items sufficiently stocked.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Part part : lowStockList) {
                sb.append("• ")
                        .append(part.getPartName())
                        .append(" (Qty: ")
                        .append(part.getPartQuantity())
                        .append(" / Min: ")
                        .append(part.getLowStockThreshold())
                        .append(")\n");
            }
            lblLowStockItems.setText(sb.toString().trim());
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
        } catch (NumberFormatException ignored) {}

        ArrayList<Part> filtered = InventoryFilter.filter(category, minPrice, maxPrice, keyword);
        inventoryData.setAll(filtered);
        tblInventory.setItems(inventoryData);
    }
}