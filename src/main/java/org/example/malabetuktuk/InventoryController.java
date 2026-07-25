package org.example.malabetuktuk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    @FXML private TextField txtThreshold;
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

        colId.setCellValueFactory(new PropertyValueFactory<Part, String>("partId"));
        colName.setCellValueFactory(new PropertyValueFactory<Part, String>("partName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Part, String>("partType"));
        colBrand.setCellValueFactory(new PropertyValueFactory<Part, String>("partBrand"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Part, Double>("partPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<Part, Integer>("partQuantity"));

        colImg.setCellFactory(new Callback<TableColumn<Part, Void>, TableCell<Part, Void>>() {
            public TableCell<Part, Void> call(TableColumn<Part, Void> param) {
                return new TableCell<Part, Void>() {
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
                            if (path != null && !path.equals("null") && !path.trim().isEmpty()) {
                                File file = new File(path);
                                if (!file.exists()) {
                                    file = new File("data/" + path);
                                }
                                if (!file.exists()) {
                                    file = new File("data/images/" + path);
                                }
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

        colAction.setCellFactory(new Callback<TableColumn<Part, Void>, TableCell<Part, Void>>() {
            public TableCell<Part, Void> call(TableColumn<Part, Void> param) {
                return new TableCell<Part, Void>() {
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Part part = getTableView().getItems().get(getIndex());
                            int currentStock = part.getPartQuantity();

                            Button btnEdit = new Button("View/Edit");
                            Button btnAdd = new Button("Add to Cart");
                            Spinner<Integer> qtySpinner = new Spinner<Integer>(1, currentStock > 0 ? currentStock : 1, 1);
                            qtySpinner.setPrefWidth(65);
                            qtySpinner.setEditable(true);

                            if (currentStock <= 0) {
                                qtySpinner.setDisable(true);
                                btnAdd.setDisable(true);
                            }

                            btnEdit.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent event) {
                                    openEditWindow(part);
                                }
                            });

                            btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                                public void handle(ActionEvent event) {
                                    int requestedQty = qtySpinner.getValue();
                                    if (part.getPartQuantity() >= requestedQty && requestedQty > 0) {
                                        part.setPartQuantity(part.getPartQuantity() - requestedQty);
                                        for (int i = 0; i < requestedQty; i++) {
                                            managerClass.addToCart(part);
                                        }
                                        PartLoad.saveItems("data/updated_data_legacy.txt");
                                        refreshScreen();
                                    } else {
                                        Alert alert = new Alert(Alert.AlertType.WARNING, "Requested quantity exceeds stock!", ButtonType.OK);
                                        alert.showAndWait();
                                    }
                                }
                            });

                            HBox box = new HBox(5, qtySpinner, btnAdd, btnEdit);
                            setGraphic(box);
                        }
                    }
                };
            }
        });

        tblInventory.setRowFactory(new Callback<TableView<Part>, TableRow<Part>>() {
            public TableRow<Part> call(TableView<Part> param) {
                TableRow<Part> row = new TableRow<Part>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && (!row.isEmpty())) {
                            openEditWindow(row.getItem());
                        }
                    }
                });
                return row;
            }
        });

        refreshScreen();

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                applyFilter();
            }
        });

        btnAddItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                openAddWindow();
            }
        });

        txtThreshold.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateLowStockAlerts();
            }
        });
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

        String currentFormattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm"));
        TextField txtDate = new TextField(currentFormattedDate);
        TextField txtImgPath = new TextField("null");

        Button btnBrowse = new Button("Browse...");
        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Product Image");
                File selectedFile = chooser.showOpenDialog(popupStage);
                if (selectedFile != null) {
                    txtImgPath.setText(selectedFile.getName());
                }
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

        grid.add(new Label("Date Added:"), 0, 6);
        grid.add(txtDate, 1, 6);

        grid.add(new Label("Image Path:"), 0, 7);
        HBox imgBox = new HBox(5, txtImgPath, btnBrowse);
        grid.add(imgBox, 1, 7);

        Button btnSubmit = new Button("Add Item");
        btnSubmit.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSubmit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    String partId = txtId.getText().trim();
                    String name = txtName.getText().trim();
                    String brand = txtBrand.getText().trim();
                    if (brand.isEmpty()) {
                        brand = "Unknown";
                    }
                    String type = txtType.getText().trim().toUpperCase();
                    String date = PartLoad.normalizeDate(txtDate.getText().trim());
                    String imgPath = txtImgPath.getText().trim();
                    if (imgPath.isEmpty()) {
                        imgPath = "null";
                    }

                    if (partId.isEmpty() || name.isEmpty() || type.isEmpty() || txtPrice.getText().trim().isEmpty() || txtQty.getText().trim().isEmpty()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill in all mandatory fields (*)", ButtonType.OK);
                        alert.showAndWait();
                        return;
                    }

                    for (Part p : PartLoad.getInventoryList()) {
                        if (p.getPartId().equalsIgnoreCase(partId)) {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Part ID already exists!", ButtonType.OK);
                            alert.showAndWait();
                            return;
                        }
                    }

                    double price = Double.parseDouble(txtPrice.getText().trim());
                    int qty = Integer.parseInt(txtQty.getText().trim());

                    Part newPart = new Part(partId, name, brand, price, qty, type, date, imgPath);
                    PartLoad.getInventoryList().add(newPart);
                    PartLoad.saveItems("data/updated_data_legacy.txt");

                    refreshScreen();
                    popupStage.close();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price or quantity formatting!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        grid.add(btnSubmit, 1, 8);

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
        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Product Image");
                File selectedFile = chooser.showOpenDialog(popupStage);
                if (selectedFile != null) {
                    txtImgPath.setText(selectedFile.getName());
                }
            }
        });

        TextField txtName = new TextField(part.getPartName());
        TextField txtType = new TextField(part.getPartType());
        TextField txtBrand = new TextField(part.getPartBrand());
        TextField txtPrice = new TextField(String.valueOf(part.getPartPrice()));
        TextField txtQty = new TextField(String.valueOf(part.getPartQuantity()));
        TextField txtDate = new TextField(part.getAddedDate());

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

        grid.add(new Label("Date Added:"), 0, 7);
        grid.add(txtDate, 1, 7);

        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    part.setPartName(txtName.getText().trim());
                    part.setPartType(txtType.getText().trim().toUpperCase());
                    String brandInput = txtBrand.getText().trim();
                    part.setPartBrand(brandInput.isEmpty() ? "Unknown" : brandInput);
                    part.setPartPrice(Double.parseDouble(txtPrice.getText().trim()));
                    part.setPartQuantity(Integer.parseInt(txtQty.getText().trim()));
                    part.setAddedDate(PartLoad.normalizeDate(txtDate.getText().trim()));
                    String imgInput = txtImgPath.getText().trim();
                    part.setImagePath(imgInput.isEmpty() ? "null" : imgInput);

                    PartLoad.saveItems("data/updated_data_legacy.txt");
                    refreshScreen();
                    popupStage.close();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price or quantity formatting!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        Button btnDelete = new Button("Delete Part");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + part.getPartName() + "?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait();

                if (confirm.getResult() == ButtonType.YES) {
                    PartLoad.getInventoryList().remove(part);
                    PartLoad.saveItems("data/updated_data_legacy.txt");
                    refreshScreen();
                    popupStage.close();
                }
            }
        });

        HBox actionBox = new HBox(10, btnSave, btnDelete);
        grid.add(actionBox, 1, 8);

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
        inventoryData.setAll(filtered);
        tblInventory.setItems(inventoryData);
    }
}