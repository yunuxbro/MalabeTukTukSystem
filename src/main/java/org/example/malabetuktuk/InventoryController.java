package org.example.malabetuktuk;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    @FXML private TableView<Part> tblInventory;
    @FXML private TableColumn<Part, Void> colImg;
    @FXML private TableColumn<Part, String> colId;
    @FXML private TableColumn<Part, String> colName;
    @FXML private TableColumn<Part, String> colCategory;
    @FXML private TableColumn<Part, String> colBrand;
    @FXML private TableColumn<Part, Double> colPrice;
    @FXML private TableColumn<Part, Integer> colQty;
    @FXML private TableColumn<Part, Void> colAction;

    private final ObservableList<Part> inventoryData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (PartLoad.getInventoryList().isEmpty()) {
            PartLoad.loadItems("data/inventory_legacy.txt");
        }

        if (cmbCategory.getItems().isEmpty()) {
            cmbCategory.getItems().addAll("All", "ENGINE", "ELECTRICAL", "BODY", "BRAKE");
            cmbCategory.setValue("All");
        }

        setupTableColumns();
        refreshScreen();

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                applyFilter();
            }
        });

        txtThreshold.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateLowStockAlerts();
            }
        });
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<Part, String>("partId"));
        colName.setCellValueFactory(new PropertyValueFactory<Part, String>("partName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Part, String>("partType"));
        colBrand.setCellValueFactory(new PropertyValueFactory<Part, String>("partBrand"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Part, Double>("partPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<Part, Integer>("partQuantity"));

        colImg.setCellFactory(new Callback<TableColumn<Part, Void>, TableCell<Part, Void>>() {
            @Override
            public TableCell<Part, Void> call(TableColumn<Part, Void> param) {
                return new TableCell<Part, Void>() {
                    private final ImageView imageView = new ImageView();
                    {
                        imageView.setFitWidth(45);
                        imageView.setFitHeight(45);
                        imageView.setPreserveRatio(true);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Part part = getTableView().getItems().get(getIndex());
                            String path = part.getImagePath();
                            Image img = null;

                            if (path != null && !path.equals("null") && !path.trim().isEmpty()) {
                                File file = new File(path);
                                if (!file.exists()) file = new File("data/" + path);
                                if (!file.exists()) file = new File("data/images/" + path);

                                if (file.exists()) {
                                    img = new Image(file.toURI().toString(), 45, 45, true, true);
                                }
                            }
                            if (img == null) {
                                img = new Image("https://via.placeholder.com/45?text=No+Img", 45, 45, true, true);
                            }
                            imageView.setImage(img);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });

        colAction.setCellFactory(new Callback<TableColumn<Part, Void>, TableCell<Part, Void>>() {
            @Override
            public TableCell<Part, Void> call(TableColumn<Part, Void> param) {
                return new TableCell<Part, Void>() {
                    private final Button btnEdit = new Button("View/Edit");
                    private final Button btnAdd = new Button("Add to Cart");
                    private final Spinner<Integer> qtySpinner = new Spinner<Integer>(1, 1, 1);
                    private final HBox box = new HBox(5, qtySpinner, btnAdd, btnEdit);

                    {
                        qtySpinner.setPrefWidth(65);
                        qtySpinner.setEditable(true);

                        btnEdit.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Part part = getTableView().getItems().get(getIndex());
                                openEditWindow(part);
                            }
                        });

                        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Part part = getTableView().getItems().get(getIndex());
                                int requestedQty = qtySpinner.getValue();

                                if (part.getPartQuantity() >= requestedQty && requestedQty > 0) {
                                    part.setPartQuantity(part.getPartQuantity() - requestedQty);

                                    for (int i = 0; i < requestedQty; i++) {
                                        managerClass.addToCart(part);
                                    }

                                    PartLoad.saveItems("data/updated_data_legacy.txt");
                                    refreshScreen();
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.WARNING,
                                            "Requested quantity exceeds stock (" + part.getPartQuantity() + ")!",
                                            ButtonType.OK);
                                    alert.showAndWait();
                                }
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= getTableView().getItems().size()) {
                            setGraphic(null);
                        } else {
                            Part part = getTableView().getItems().get(getIndex());
                            int currentStock = part.getPartQuantity();

                            if (currentStock > 0) {
                                SpinnerValueFactory<Integer> valueFactory =
                                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, currentStock, 1);
                                qtySpinner.setValueFactory(valueFactory);
                                qtySpinner.setDisable(false);
                                btnAdd.setDisable(false);
                            } else {
                                qtySpinner.setDisable(true);
                                btnAdd.setDisable(true);
                            }

                            setGraphic(box);
                        }
                    }
                };
            }
        });

        tblInventory.setRowFactory(new Callback<TableView<Part>, TableRow<Part>>() {
            @Override
            public TableRow<Part> call(TableView<Part> param) {
                final TableRow<Part> row = new TableRow<Part>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && (!row.isEmpty())) {
                            Part rowData = row.getItem();
                            openEditWindow(rowData);
                        }
                    }
                });
                return row;
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

    private void openEditWindow(final Part part) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Product Details - " + part.getPartName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        final ImageView imgView = new ImageView();
        imgView.setFitWidth(120);
        imgView.setFitHeight(120);
        imgView.setPreserveRatio(true);

        String path = part.getImagePath();
        if (path != null && !path.equals("null") && !path.trim().isEmpty()) {
            File f = new File(path);
            if (!f.exists()) f = new File("data/" + path);
            if (!f.exists()) f = new File("data/images/" + path);
            if (f.exists()) {
                imgView.setImage(new Image(f.toURI().toString(), 120, 120, true, true));
            }
        }

        final TextField txtImgPath = new TextField(part.getImagePath());
        Button btnBrowse = new Button("Browse Image...");

        btnBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Product Image");
                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
                );
                File selectedFile = chooser.showOpenDialog(popupStage);
                if (selectedFile != null) {
                    txtImgPath.setText(selectedFile.getName());
                    imgView.setImage(new Image(selectedFile.toURI().toString(), 120, 120, true, true));
                }
            }
        });

        final TextField txtName = new TextField(part.getPartName());
        final TextField txtType = new TextField(part.getPartType());
        final TextField txtBrand = new TextField(part.getPartBrand());
        final TextField txtPrice = new TextField(String.valueOf(part.getPartPrice()));
        final TextField txtQty = new TextField(String.valueOf(part.getPartQuantity()));

        grid.add(new Label("Image Preview:"), 0, 0);
        grid.add(imgView, 1, 0);

        grid.add(new Label("Image Path/Name:"), 0, 1);
        HBox imgBox = new HBox(5, txtImgPath, btnBrowse);
        grid.add(imgBox, 1, 1);

        grid.add(new Label("Part ID:"), 0, 2);
        grid.add(new Label(part.getPartId()), 1, 2);

        grid.add(new Label("Part Name:"), 0, 3);
        grid.add(txtName, 1, 3);

        grid.add(new Label("Category Type:"), 0, 4);
        grid.add(txtType, 1, 4);

        grid.add(new Label("Brand:"), 0, 5);
        grid.add(txtBrand, 1, 5);

        grid.add(new Label("Price (Rs.):"), 0, 6);
        grid.add(txtPrice, 1, 6);

        grid.add(new Label("Stock Quantity:"), 0, 7);
        grid.add(txtQty, 1, 7);

        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    part.setPartName(txtName.getText().trim());
                    part.setPartType(txtType.getText().trim());
                    part.setPartBrand(txtBrand.getText().trim());
                    part.setPartPrice(Double.parseDouble(txtPrice.getText().trim()));
                    part.setPartQuantity(Integer.parseInt(txtQty.getText().trim()));
                    part.setImagePath(txtImgPath.getText().trim());

                    PartLoad.saveItems("data/updated_data_legacy.txt");
                    refreshScreen();
                    popupStage.close();
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price or quantity number formatting!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        });

        grid.add(btnSave, 1, 8);

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