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

import org.example.malabetuktuk.blueprints.Part;
import org.example.malabetuktuk.loadFiles.PartLoad;
import org.example.malabetuktuk.CRUD.AddItem;
import org.example.malabetuktuk.CRUD.UpdateItem;
import org.example.malabetuktuk.CRUD.DeleteItem;
import org.example.malabetuktuk.POS.InventoryFilter;
import org.example.malabetuktuk.POS.LowStock;
import org.example.malabetuktuk.POS.managerClass;
import org.example.malabetuktuk.sorts.itemSort;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class InventoryController {

    @FXML private Label lblTotalItems, lblTotalValue, lblLowStockItems;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtMinPrice, txtMaxPrice, txtKeyword;
    @FXML private Button btnSearch, btnAddItem;

    @FXML private TableView<Part> tblInventory;
    @FXML private TableColumn<Part, Void> colImg, colAction;
    @FXML private TableColumn<Part, String> colId, colName, colCategory, colBrand, colDate;
    @FXML private TableColumn<Part, Double> colPrice;
    @FXML private TableColumn<Part, Integer> colQty;

    private final ObservableList<Part> inventoryData = FXCollections.observableArrayList();
    private static final String FILE_PATH = "data/updated_data_legacy.txt";

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

        if (colDate != null) {
            colDate.setCellValueFactory(new PropertyValueFactory<>("addedDate"));
        }

        setupImageColumn();
        setupActionColumn();

        tblInventory.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    openWindow(row.getItem());
                }
            });
            return row;
        });

        refreshScreen();
        btnSearch.setOnAction(e -> applyFilter());
        btnAddItem.setOnAction(e -> openWindow(null));
    }

    private void refreshScreen() {
        itemSort.sortInventory();
        inventoryData.setAll(PartLoad.getInventoryList());
        tblInventory.setItems(inventoryData);
        updateKPIsAndAlerts();
    }

    private void setupImageColumn() {
        colImg.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Part part = getTableView().getItems().get(getIndex());
                File file = getImageFile(part.getImagePath());

                if (file != null && file.exists()) {
                    ImageView imgView = new ImageView(new Image(file.toURI().toString(), 45, 45, true, true));
                    setGraphic(imgView);
                } else {
                    Label noImgLabel = new Label("No Image");
                    noImgLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                    setGraphic(noImgLabel);
                }
            }
        });
    }

    private File getImageFile(String path) {
        if (path == null || path.equalsIgnoreCase("null") || path.trim().isEmpty()) {
            return null;
        }
        File f = new File(path);
        if (!f.exists()) {
            f = new File("data/" + path);
        }
        if (!f.exists()) {
            f = new File("data/images/" + path);
        }
        return f;
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Part part = getTableView().getItems().get(getIndex());
                int stock = part.getPartQuantity();

                Button btnEdit = new Button("View/Edit");
                Button btnAdd = new Button("Add to Cart");

                if (stock <= 0) {
                    btnAdd.setDisable(true);
                }

                btnEdit.setOnAction(e -> openWindow(part));
                btnAdd.setOnAction(e -> {
                    if (managerClass.validateAddToCart(part, 1)) {
                        part.setPartQuantity(stock - 1);
                        managerClass.addToCart(part);
                        PartLoad.saveItems(FILE_PATH);
                        refreshScreen();
                    }
                });

                setGraphic(new HBox(5, btnAdd, btnEdit));
            }
        });
    }

    private void updateKPIsAndAlerts() {
        int totalQty = 0;
        double totalVal = 0.0;
        for (Part p : PartLoad.getInventoryList()) {
            totalQty += p.getPartQuantity();
            totalVal += (p.getPartPrice() * p.getPartQuantity());
        }

        lblTotalItems.setText(String.valueOf(totalQty));
        lblTotalValue.setText(String.format("Rs. %.2f", totalVal));

        ArrayList<Part> lowStockList = LowStock.getLowStockParts();
        if (lowStockList.isEmpty()) {
            lblLowStockItems.setText("All items sufficiently stocked.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Part p : lowStockList) {
                sb.append("• ").append(p.getPartName())
                        .append(" (Qty: ").append(p.getPartQuantity())
                        .append(" / Min Threshold: ").append(p.getLowStockThreshold()).append(")\n");
            }
            lblLowStockItems.setText(sb.toString().trim());
        }
    }

    private void applyFilter() {
        double min = 0.0;
        double max = Double.MAX_VALUE;

        if (!txtMinPrice.getText().trim().isEmpty()) {
            try {
                min = Double.parseDouble(txtMinPrice.getText().trim());
            } catch (NumberFormatException ignored) {}
        }
        if (!txtMaxPrice.getText().trim().isEmpty()) {
            try {
                max = Double.parseDouble(txtMaxPrice.getText().trim());
            } catch (NumberFormatException ignored) {}
        }

        inventoryData.setAll(InventoryFilter.filter(cmbCategory.getValue(), min, max, txtKeyword.getText()));
        tblInventory.setItems(inventoryData);
    }

    private void openWindow(Part part) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        if (part != null) {
            stage.setTitle("Edit Product - " + part.getPartName());
        } else {
            stage.setTitle("Add New Item");
        }

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
        TextField txtImgPath = new TextField();
        TextField txtThreshold = new TextField("5");

        String currentFormattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
        TextField txtDate = new TextField(currentFormattedDate);

        if (part != null) {
            txtId.setText(part.getPartId());
            txtId.setDisable(true);
            txtName.setText(part.getPartName());
            txtBrand.setText(part.getPartBrand());
            txtPrice.setText(String.valueOf(part.getPartPrice()));
            txtQty.setText(String.valueOf(part.getPartQuantity()));
            txtType.setText(part.getPartType());
            txtImgPath.setText(part.getImagePath());
            txtThreshold.setText(String.valueOf(part.getLowStockThreshold()));
            if (part.getAddedDate() != null) {
                txtDate.setText(part.getAddedDate());
            }
        } else {
            txtImgPath.setText("null");
        }

        Label lblError = new Label();
        lblError.setTextFill(Color.RED);

        Button btnBrowse = new Button("Browse...");
        btnBrowse.setOnAction(e -> {
            File selected = new FileChooser().showOpenDialog(stage);
            if (selected != null) {
                txtImgPath.setText(selected.getName());
            }
        });

        grid.add(new Label("Part ID:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Part Name:"), 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(new Label("Brand:"), 0, 2);
        grid.add(txtBrand, 1, 2);
        grid.add(new Label("Price (Rs.):"), 0, 3);
        grid.add(txtPrice, 1, 3);
        grid.add(new Label("Stock Qty:"), 0, 4);
        grid.add(txtQty, 1, 4);
        grid.add(new Label("Low Stock Threshold:"), 0, 5);
        grid.add(txtThreshold, 1, 5);
        grid.add(new Label("Category Type:"), 0, 6);
        grid.add(txtType, 1, 6);
        grid.add(new Label("Date Added:"), 0, 7);
        grid.add(txtDate, 1, 7);
        grid.add(new Label("Image Path:"), 0, 8);
        grid.add(new HBox(5, txtImgPath, btnBrowse), 1, 8);
        grid.add(lblError, 0, 9, 2, 1);

        Button btnSubmit = new Button();
        if (part != null) {
            btnSubmit.setText("Save Changes");
        } else {
            btnSubmit.setText("Add Item");
        }

        btnSubmit.setOnAction(e -> {
            int threshold = 5;
            try {
                threshold = Integer.parseInt(txtThreshold.getText().trim());
                if (threshold <= 0) {
                    lblError.setText("Threshold must be a positive integer.");
                    return;
                }
            } catch (NumberFormatException ex) {
                lblError.setText("Invalid low stock threshold value.");
                return;
            }

            String res;
            if (part != null) {
                res = UpdateItem.executeUpdate(part.getPartId(), txtName.getText(), txtBrand.getText(), txtPrice.getText(), txtQty.getText(), txtType.getText(), txtImgPath.getText(), FILE_PATH);
            } else {
                res = AddItem.executeAdd(txtId.getText(), txtName.getText(), txtBrand.getText(), txtPrice.getText(), txtQty.getText(), txtType.getText(), txtDate.getText(), txtImgPath.getText(), FILE_PATH);
            }

            if ("SUCCESS".equals(res)) {
                if (part != null) {
                    part.setLowStockThreshold(threshold);
                } else {
                    for (Part p : PartLoad.getInventoryList()) {
                        if (p.getPartId().equalsIgnoreCase(txtId.getText().trim())) {
                            p.setLowStockThreshold(threshold);
                            break;
                        }
                    }
                }
                PartLoad.saveItems(FILE_PATH);
                refreshScreen();
                stage.close();
            } else {
                lblError.setText(res);
            }
        });

        HBox actions = new HBox(10, btnSubmit);

        if (part != null) {
            Button btnDelete = new Button("Delete");
            btnDelete.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            btnDelete.setOnAction(e -> {
                if (DeleteItem.executeDelete(part.getPartId(), FILE_PATH)) {
                    refreshScreen();
                    stage.close();
                } else {
                    lblError.setText("Failed to delete item.");
                }
            });
            actions.getChildren().add(btnDelete);
        }

        grid.add(actions, 1, 10);
        stage.setScene(new Scene(grid));
        stage.showAndWait();
    }
}