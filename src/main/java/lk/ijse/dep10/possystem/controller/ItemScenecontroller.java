package lk.ijse.dep10.possystem.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.Printer;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.Item;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import net.sourceforge.barbecue.output.OutputException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemScenecontroller {

    public ComboBox<String> bikeModel;
    public ComboBox<String> partsCategory;
    public ListView<String> lstParts;
    public Button btnForward;
    public Button btnBack;
    public ListView<String> lstSelectedPart;
    public TextField txtAdd;
    public ListView<String> lstModel;
    public Button btnAdd;
    public Button btnErace;
    public ComboBox<String> cmbBrand;
    public TextField txtSupplierPrice;
    public TextField txtProfitPercentage;
    public Button btnNetPrice;
    public Button btnSellingPrice;
    public Button btnProfitPerItem;
    public Button btnAddBrand;
    public Button btnAddModel;
    public Button btnAddCategory;
    public TextField txtSearch;
    public Button btnBatchNumber;
    public Button btnClearAll;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnSave;

    @FXML
    private DatePicker dtpBought;

    @FXML
    private DatePicker dtpDate;

    @FXML
    private ImageView imgBarcode;

    @FXML
    private TableView<Item> tblSummary;

    @FXML
    private TextField txtBatchNo;

    @FXML
    private TextField txtDiscount;

    @FXML
    private TextField txtItemCode;

    @FXML
    private TextField txtNetPrice;

    @FXML
    private TextField txtProfit;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtSellingPrice;
    @FXML
    private TextField txtUsrAdmin;
    private ArrayList<Item> loadedDetails = new ArrayList<>();
    private JasperReport jasperReport;

    public void initialize() {

        tblSummary.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("role"));
        tblSummary.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("batchNo"));
        tblSummary.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblSummary.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("model"));
        tblSummary.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        tblSummary.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("netPrice"));
        tblSummary.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblSummary.getColumns().get(7).setCellValueFactory(new PropertyValueFactory<>("discount"));
        tblSummary.getColumns().get(8).setCellValueFactory(new PropertyValueFactory<>("dateOfBought"));
        tblSummary.getColumns().get(9).setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        tblSummary.getColumns().get(10).setCellValueFactory(new PropertyValueFactory<>("profit"));

        txtDiscount.textProperty().addListener((observableValue, s, current) -> {

            txtNetPrice.setText(netPrice());

        });


        txtProfitPercentage.textProperty().addListener((observableValue, s, current) -> {

            txtSellingPrice.setText(sellingPriceFinal());


        });

        txtSellingPrice.textProperty().addListener((observableValue, s, current) -> {

            if (current != null) {
                txtProfit.setText(profitPerItem().toString());
            }

        });

        loadItems();
        ObservableList<String> items = lstSelectedPart.getItems();
        items.addListener((ListChangeListener<? super String>) observable -> {
            if (items.size() == 1) {
                btnForward.setDisable(true);
            } else {
                btnForward.setDisable(false);
            }
        });

//        txtItemCode.textProperty().addListener((observableValue, s, current) ->{
//            if(current!=null) {
//
//            }
//        } );

        cmbBrand.getItems().addAll(brands());

        cmbBrand.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
//            if (previous != null) {
//                bikeModel.getItems().clear();
//
//            }
            if (current != null) {
                bikeModel.getItems().clear();
                bikeModel.getItems().addAll(listOfBikes());
                bikeModel.requestFocus();
            }
        });

        bikeModel.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
//            if (previous != null) {
//                partsCategory.getItems().clear();
//            }
            if (current != null) {
                partsCategory.getItems().clear();
                partsCategory.getItems().addAll(categoryList());
                partsCategory.requestFocus();
            }
        });

        partsCategory.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
//            if (previous != null) {
//                lstParts.getItems().clear();
//                lstSelectedPart.getItems().clear();
//            }
            if (current != null) {
                lstParts.getItems().clear();
                lstSelectedPart.getItems().clear();
                lstParts.getItems().addAll(addPartsToSelection());
            }
        });

        lstSelectedPart.getSelectionModel().selectedItemProperty().addListener((observableValue, s, current) -> {

            if (current != null) {
                btnBack.setDisable(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please Select a Item");
                alert.showAndWait();
                return;
            }

        });
        dtpDate.setValue(LocalDate.now());

        txtBatchNo.textProperty().addListener((observableValue, s, current) -> {

            if (txtBatchNo.getText().length() != 1 && !(txtBatchNo.getText().isEmpty())) {
                setDate(current);
                if (!(validationOfBatchNumber())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please Re-Check and Enter the Valid Batch Number");
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.OK) {
                        txtBatchNo.selectAll();
                        txtBatchNo.requestFocus();
                    }
                }

            } else {
                dtpBought.setValue(null);
            }
        });
        tblSummary.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            if (current != null) {
//               responsiveList();
                btnRemove.setDisable(false);
                loadAllItemsToUpdate();
            }
        });

        generateBarCode();
        txtItemCode.textProperty().addListener(c -> generateBarCode());
        initializeJasperReport();

        txtSearch.textProperty().addListener((observableValue, s, current) -> {
            if (current != null)
                searchItem(current);

        });

        lk.ijse.dep10.possystem.model.User principal = (lk.ijse.dep10.possystem.model.User) System.getProperties().get("principal");
        txtUsrAdmin.setText(String.format("%s: %s", principal.getRole().name(), principal.getFullName()));
    }

    private void setDate(String batchNumber) {
        ArrayList<String> dates = new ArrayList<>();
        int enteredBatchNumber = Integer.parseInt(batchNumber);
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement("SELECT *FROM Batches WHERE batch_no=?");
            prd.setInt(1, enteredBatchNumber);
            ResultSet rst = prd.executeQuery();
            while (rst.next()) {
                Date date = rst.getDate(4);
                String dateInStringFormat = date.toString();
                dates.add(dateInStringFormat);
            }

            for (String date : dates) {
                dtpBought.setValue(LocalDate.parse(date));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeJasperReport() {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/report/barcodeprint.jrxml"));
            jasperReport = JasperCompileManager.compileReport(jasperDesign);

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validationOfBatchNumber() {
        boolean enteredBatchNumber = true;
        if (!(txtBatchNo.getText().isEmpty())) {
            int batchNumber = Integer.parseInt(txtBatchNo.getText());
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT *FROM Batches WHERE batch_no=?";
            try {
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setInt(1, batchNumber);
                ResultSet rst = prd.executeQuery();

                if (rst.next()) {
                    enteredBatchNumber = true;
                } else {
                    enteredBatchNumber = false;
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return enteredBatchNumber;
    }

    private ArrayList<String> categoryList() {
        ArrayList<String> partsCategoryList = new ArrayList<>();
        String sql = "SELECT *FROM Parts_Category";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {

                String category = rst.getString(1);
                partsCategoryList.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return partsCategoryList;
    }

    private ArrayList<String> listOfBikes() {

        ArrayList<String> bikeList = new ArrayList<>();
        String selectedItem = cmbBrand.getSelectionModel().getSelectedItem();
        String sql = "SELECT bike FROM List_Of_Bikes WHERE brand_name=?";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, selectedItem);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String bikeNames = rst.getString(1);
                bikeList.add(bikeNames);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bikeList;
    }

    private ArrayList<String> addPartsToSelection() {

        ArrayList<String> selectionOfParts = new ArrayList<>();
        String sql = "SELECT *FROM Parts WHERE parts_category=?";

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, partsCategory.getSelectionModel().getSelectedItem());
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String items = rst.getString(2);
                selectionOfParts.add(items);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return selectionOfParts;
    }

    private ArrayList<String> brands() {
        ArrayList<String> brandsOfBikes = new ArrayList<>();
        String sql = "SELECT *FROM Brands";

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String brandName = rst.getString(1);
                //BrandNames brandNames = new BrandNames(brandName);
                brandsOfBikes.add(brandName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return brandsOfBikes;
    }

    private boolean itemCodeValidation() {
        boolean itemCodeValidate = true;
        String batchNo = txtBatchNo.getText();
        try {
            if (!(Integer.parseInt(batchNo) > 10)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter Valid Batch Number", ButtonType.OK);
                alert.showAndWait();
                txtBatchNo.selectAll();
                System.out.println("selectAll");
                txtBatchNo.requestFocus();
                itemCodeValidate = false;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter Valid Batch Number");
            alert.showAndWait();
            txtBatchNo.selectAll();
            txtBatchNo.requestFocus();
            itemCodeValidate = false;

        }
        return itemCodeValidate;
    }

    private void itemCode() {

        String batchNo = txtBatchNo.getText();
        String initial = "1";
        if (!(itemCodeValidation())) return;
        String newNumber = batchNo + initial;

        ArrayList<Long> list = new ArrayList<>();
        ObservableList<Item> items = tblSummary.getItems();
        if (items.isEmpty() || comparisonNumbers()) {
            int lengthOfItemNo = 12 - (newNumber.length());
            StringBuilder sb = new StringBuilder();
            sb.append(4);
            for (int i = 0; i < lengthOfItemNo - 1; i++) {
                sb.append("0");
            }
            sb.append(newNumber);
            String newNumber1 = sb.toString();
            txtItemCode.setText(newNumber1);
        } else {
            for (Item item : items) {
                int batchNo1 = item.getBatchNo();
                if (batchNo1 == Integer.parseInt(txtBatchNo.getText())) {
                    Long itemCode = item.getItemCode();
                    list.add(itemCode);
                }
            }
            Long lastItemCode = list.get(list.size() - 1);
//            int lstItemCode = Integer.parseInt(lastItemCode);
            Long newItemCode = Math.addExact(lastItemCode, 1);
            String strNewItemCode = Long.toString(newItemCode);
            int lengthOfAddingZero = 12 - strNewItemCode.length();
            StringBuilder sb1 = new StringBuilder();


            for (int i = 0; i < lengthOfAddingZero - 1; i++) {
                sb1.append("0");
            }
            sb1.append(strNewItemCode);
            txtItemCode.setText(sb1.toString());
        }
    }

    private void loadItems() {
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM Items";
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery(sql);
            while (rst.next()) {
                String role = rst.getString(1);
                int batchNumber = rst.getInt(2);

                Long itemCode = rst.getLong(3);

                String model = rst.getString(6);
                String itemName = rst.getString(7);
                BigDecimal supplierPrice = rst.getBigDecimal(8);
                BigDecimal netPrice = rst.getBigDecimal(9);
                int quantity = rst.getInt(10);
                BigDecimal discount = rst.getBigDecimal(11);
                Date date = rst.getDate(13);
                BigDecimal sellingPrice = rst.getBigDecimal(14);
                BigDecimal profit = rst.getBigDecimal(15);

                Item item = new Item(role, batchNumber, itemCode, model, itemName, netPrice, quantity, discount, date, sellingPrice, profit);
                tblSummary.getItems().add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean comparisonNumbers() {
        boolean itemNumber = true;
        ObservableList<Item> items = tblSummary.getItems();
        for (Item object : items) {
            int batchNo = object.getBatchNo();
            if (batchNo == Integer.parseInt(txtBatchNo.getText())) {
                itemNumber = false;
            } else {
                itemNumber = true;
            }

        }
        return itemNumber;
    }

    private BigDecimal sellingPrice() {
        BigDecimal value = BigDecimal.valueOf(100);
        String netPrice = txtNetPrice.getText();
        BigDecimal netPriceDecimal = new BigDecimal(netPrice);

        String proPercentage = txtProfitPercentage.getText();
        BigDecimal profitPercentage = new BigDecimal(proPercentage);

        BigDecimal divided = profitPercentage.divide(value);
        BigDecimal sellingPrice = divided.multiply(netPriceDecimal).add(netPriceDecimal);

        return sellingPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private void searchItem(String searchItems) {


        Connection connection = DBConnection.getInstance().getConnection();


        String searchSql = "SELECT *FROM Items WHERE role LIKE '%1$s' OR batch_num LIKE '%1$s' OR item_code LIKE '%1$s' OR model LIKE '%1$s' OR item_name LIKE '%1$s' OR qty LIKE '%1$s' OR date_bought LIKE '%1$s' ";
        searchSql = String.format(searchSql, '%' + searchItems + '%');
        try {
            PreparedStatement prd = connection.prepareStatement(searchSql);
            ResultSet rst = prd.executeQuery();
            tblSummary.getItems().clear();

            while (rst.next()) {
                String role = rst.getString(1);
                int batchNumber = rst.getInt(2);
                long itemCode = rst.getLong(3);
                String model = rst.getString(6);
                String itemName = rst.getString(7);
                BigDecimal netPrice = rst.getBigDecimal(9);
                int qty = rst.getInt(10);
                BigDecimal discount = rst.getBigDecimal(11);
                Date date = rst.getDate(13);
                BigDecimal sellingPrice = rst.getBigDecimal(14);
                BigDecimal profitPerItem = rst.getBigDecimal(15);

                Item item = new Item(role, batchNumber, itemCode, model, itemName, netPrice, qty, discount, date, sellingPrice, profitPerItem);
                tblSummary.getItems().addAll(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal profitPerItem() {
        BigDecimal profitPerItem = BigDecimal.valueOf(0);
        if (netPrice() != null) {
            BigDecimal netPrice = new BigDecimal(netPrice());
            BigDecimal sellingPrice = new BigDecimal(sellingPriceFinal());
            profitPerItem = sellingPrice.subtract(netPrice);

        }
        return profitPerItem.setScale(2, RoundingMode.HALF_UP);

    }

    private boolean dataValidate() {
        boolean isDataValid = true;

        if (!(txtBatchNo.getText().matches("\\d+"))) {
            txtBatchNo.selectAll();
            txtBatchNo.requestFocus();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter the Correct Batch Number");
            alert.showAndWait();
            isDataValid = false;
        }
        if (cmbBrand.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Select A Brand");
            alert.showAndWait();
            cmbBrand.requestFocus();
            isDataValid = false;
        }
        if (bikeModel.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Select A Model");
            alert.showAndWait();
            bikeModel.requestFocus();
            isDataValid = false;
        }
        if (partsCategory.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Select A Parts Category");
            alert.showAndWait();
            partsCategory.requestFocus();
            isDataValid = false;
        }
        if (!(txtSupplierPrice.getText().matches("\\d+(?:\\.\\d{2})?"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Add Correct Value");
            alert.showAndWait();
            txtSupplierPrice.selectAll();
            txtSupplierPrice.requestFocus();
            isDataValid = false;
        }
        if (!(txtDiscount.getText().matches("\\d+(?:\\.\\d{2})?"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Add Correct Value");
            alert.showAndWait();
            txtDiscount.selectAll();
            txtDiscount.requestFocus();
            isDataValid = false;
        }
        if (!(txtProfitPercentage.getText().matches("\\d+(?:\\.\\d{2})?"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Add Correct Value");
            alert.showAndWait();
            txtProfitPercentage.selectAll();
            txtProfitPercentage.requestFocus();
            isDataValid = false;
        }
        if (!(txtQuantity.getText().matches("\\d+"))) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Add Correct Value");
            alert.showAndWait();
            txtQuantity.selectAll();
            txtQuantity.requestFocus();
            isDataValid = false;
        }
        if (dtpBought.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Add Bought Date");
            alert.showAndWait();
            dtpBought.requestFocus();
            isDataValid = false;
        }
        return isDataValid;
    }

    private void loadAllItemsToUpdate() {

        String[] collection = new String[5];
        int selectedIndex = tblSummary.getSelectionModel().getSelectedIndex();
        Item item = tblSummary.getItems().get(selectedIndex);
        String date = item.getDateOfBought().toString();

        LocalDate localDate = LocalDate.parse(date);


        String sql = "SELECT *FROM Items WHERE item_code=?";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setLong(1, item.getItemCode());
            ResultSet rst = prd.executeQuery();
            while (rst.next()) {
                String brandName = rst.getString("brand_name");
                String partsCategory = rst.getString("parts_category");
                String itemName = rst.getString("item_name");
                BigDecimal supplierPrice = rst.getBigDecimal("supplier_price");
                BigDecimal profitPercentage = rst.getBigDecimal("profit_percentage");

                collection[0] = brandName;
                collection[1] = partsCategory;
                collection[2] = supplierPrice.toString();
                collection[3] = itemName;
                collection[4] = profitPercentage.toString();
            }

            txtBatchNo.setText(Integer.toString(item.getBatchNo()));
            txtItemCode.setText(Long.toString(item.getItemCode()));

            txtSupplierPrice.setText(collection[2]);
            cmbBrand.setValue(collection[0]);
            txtUsrAdmin.setText(item.getRole());
            bikeModel.setValue(item.getModel());
            partsCategory.setValue(collection[1]);

            if (lstSelectedPart.getItems() != null) {
                lstSelectedPart.getItems().clear();
                lstSelectedPart.getItems().add(collection[3]);
            }
            txtNetPrice.setText(item.getNetPrice().setScale(2, RoundingMode.HALF_UP).toString());
            txtDiscount.setText(item.getDiscount().toString());
            txtProfitPercentage.setText(collection[4]);
            txtQuantity.setText(Integer.toString(item.getQty()));
            dtpBought.setValue(localDate);
            txtSellingPrice.setText(item.getSellingPrice().setScale(2, RoundingMode.HALF_UP).toString());
            txtProfit.setText(item.getProfit().toString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void responsiveList() {

        if (lstSelectedPart.getItems() != null) {
            ObservableList<String> items = lstSelectedPart.getItems();
            for (String item : items) {
                ObservableList<String> items1 = lstParts.getItems();
                for (String itemName : items1) {
                    if (itemName.contains(item)) {
                        items1.remove(item);
                    }
                }
            }
        }
    }

    private void clearAll() {
        txtNetPrice.clear();
        txtQuantity.clear();
        txtDiscount.clear();
        txtSupplierPrice.clear();
        txtSellingPrice.clear();
        txtProfit.clear();
        txtProfitPercentage.clear();
        dtpBought.setValue(null);
        cmbBrand.getItems().clear();
        partsCategory.getItems().clear();
        lstParts.getItems().clear();
        lstSelectedPart.getItems().clear();
        txtItemCode.clear();
        txtBatchNo.clear();
        txtUsrAdmin.clear();
        imgBarcode.setImage(null);
        bikeModel.getItems().clear();

    }

    @FXML
    void btnNewOnAction(ActionEvent event) {
        btnBack.setDisable(true);
        btnRemove.setDisable(true);
        txtNetPrice.clear();
        txtQuantity.clear();
        txtDiscount.clear();
        txtSupplierPrice.clear();
        txtSellingPrice.clear();
        txtProfit.clear();
        txtProfitPercentage.clear();
        if (validationOfBatchNumber()) {
            itemCode();
        } else {
            txtBatchNo.requestFocus();
            txtBatchNo.selectAll();
        }
        if (itemCodeValidation()) {
            cmbBrand.requestFocus();
        }
//        cmbBrand.getItems().addAll(brands());
    }

    @FXML
    void btnPrintOnAction(ActionEvent event) throws IOException {
        Image picture = imgBarcode.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(picture, null);

        try {
            JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/report/barcodeprint.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            HashMap<String, Object> reportParams = new HashMap<>();
            reportParams.put("profilePicture", bufferedImage);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams, new JREmptyDataSource(1));
            JasperViewer.viewReport(jasperPrint, false);

            Printer defaultPrinter = Printer.getDefaultPrinter();
            if (defaultPrinter==null){
                new Alert(Alert.AlertType.ERROR,"no printer has been configured").showAndWait();
                return;
            }
            JasperPrintManager.printReport(jasperPrint,true);

        }catch (JRException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        Item selectedItem = tblSummary.getSelectionModel().getSelectedItem();
        tblSummary.getItems().remove(selectedItem);

        String sql = "DELETE FROM Items WHERE item_code=?";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setLong(1, selectedItem.getItemCode());
            prd.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!(dataValidate())) return;
        int batchNo = Integer.parseInt(txtBatchNo.getText());
        Long itemCode = Long.parseLong(txtItemCode.getText());
        String model = bikeModel.getSelectionModel().getSelectedItem();
        String itemName = selectPart();

        String profitPercentage = txtProfitPercentage.getText();
        BigDecimal profitPercentageValue = new BigDecimal(profitPercentage);

        String price = txtNetPrice.getText();
        String supplierPrice = txtSupplierPrice.getText();
        BigDecimal supplierValue = new BigDecimal(supplierPrice);
        BigDecimal netPrice = new BigDecimal(price);


        String selectedCategory = partsCategory.getSelectionModel().getSelectedItem();

        String selectedBrand = cmbBrand.getSelectionModel().getSelectedItem();


        int quantity = Integer.parseInt(txtQuantity.getText());
        String discount = txtDiscount.getText();
        BigDecimal discount1 = new BigDecimal(discount);
        Date boughtDate = Date.valueOf(dtpBought.getValue());
        BigDecimal sellingPrice= sellingPrice();
        BigDecimal price2 = sellingPrice.multiply(new BigDecimal(quantity));

        Item item = new Item(txtUsrAdmin.getText(), batchNo, itemCode, model, itemName, netPrice, quantity, discount1, boughtDate, sellingPrice(), profitPerItem());

        Item selectedItem = tblSummary.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO Items (role, batch_num, item_code,brand_name,parts_category, model, item_name, supplier_price,  net_price, qty, discount,profit_percentage, date_bought, selling_price, profit,price) value (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setString(1, txtUsrAdmin.getText());
                prd.setInt(2, batchNo);
                prd.setLong(3, itemCode);
                prd.setString(4, selectedBrand);
                prd.setString(5, selectedCategory);
                prd.setString(6, model);
                prd.setString(7, itemName);
                prd.setBigDecimal(8, supplierValue);
                prd.setBigDecimal(9, netPrice);
                prd.setInt(10, quantity);
                prd.setBigDecimal(11, discount1);
                prd.setBigDecimal(12, profitPercentageValue);
                prd.setDate(13, boughtDate);
                prd.setBigDecimal(14, sellingPrice());
                prd.setBigDecimal(15, profitPerItem());
                prd.setBigDecimal(16,price2);

                tblSummary.getItems().add(item);
                prd.executeUpdate();
                clearAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sqlUpdate = "UPDATE Items SET role=?,batch_num=?,item_code=?,brand_name=?, parts_category=?,model=?,item_name=?,supplier_price=?, net_price=?,qty=?,discount=?,profit_percentage=?, date_bought=?,selling_price=?,profit=?,price=? WHERE item_code=?";
            Connection connection1 = DBConnection.getInstance().getConnection();
            Long itemCode1 = selectedItem.getItemCode();

            try {
                PreparedStatement prd = connection1.prepareStatement(sqlUpdate);

                prd.setString(1, txtUsrAdmin.getText());
                prd.setInt(2, batchNo);
                prd.setLong(3, itemCode);
                prd.setString(4, selectedBrand);
                prd.setString(5, selectedCategory);
                prd.setString(6, model);
                prd.setString(7, itemName);
                prd.setBigDecimal(8, supplierValue);
                prd.setBigDecimal(9, netPrice);
                prd.setInt(10, quantity);
                prd.setBigDecimal(11, discount1);
                prd.setBigDecimal(12, profitPercentageValue);
                prd.setDate(13, boughtDate);
                prd.setBigDecimal(14, sellingPrice());
                prd.setBigDecimal(15, profitPerItem());
                prd.setBigDecimal(16,price2);
                prd.setLong(17, itemCode1);

                tblSummary.getItems().remove(selectedItem);
                tblSummary.getItems().add(item);
                prd.executeUpdate();
                clearAll();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void btnForwardOnAction(ActionEvent actionEvent) {
        int selectedIndex = lstParts.getSelectionModel().getSelectedIndex();
        String removedString = lstParts.getItems().remove(selectedIndex);
        lstSelectedPart.getItems().add(removedString);
    }

    public void btnBackOnAction(ActionEvent actionEvent) {
        if (!(lstSelectedPart.getItems().isEmpty())) {
            int selectedIndex = lstSelectedPart.getSelectionModel().getSelectedIndex();
            String remove = lstSelectedPart.getItems().remove(selectedIndex);
            lstParts.getItems().add(remove);
        }
    }

    private String selectPart() {
        StringBuilder sr = new StringBuilder();
        ObservableList<String> items = lstSelectedPart.getItems();
        for (String item : items) {
            sr.append(item);
        }
        return sr.toString();
    }

    private String netPrice() {
        String[] priceList = new String[1];
        String supplierPrice = txtSupplierPrice.getText();
        String discount = txtDiscount.getText();
        if (discount.matches("\\d+(?:.\\d+)?") && supplierPrice.matches("\\d+(?:.\\d+)?")) {
            BigDecimal profitPercentage = new BigDecimal(discount);
            BigDecimal supplierGivePrice = new BigDecimal(supplierPrice);
            BigDecimal value = BigDecimal.valueOf(100.00);
            BigDecimal divide = profitPercentage.divide(value);
            System.out.println(divide);
            BigDecimal result = divide.multiply(supplierGivePrice);
            BigDecimal subtract = supplierGivePrice.subtract(result).setScale(2, RoundingMode.HALF_UP);
            String price = subtract.toString();
            priceList[0] = price;

        }
        return priceList[0];
    }

    private String sellingPriceFinal() {

        String[] sellingPriceList = new String[1];
        String netPrice = txtNetPrice.getText();
        String profitPercentage = txtProfitPercentage.getText();
        if (profitPercentage.matches("\\d+(?:\\.\\d{2})?")) {
            BigDecimal valueOfProfitPercentage = new BigDecimal(profitPercentage);
            BigDecimal subNetPrice = new BigDecimal(netPrice);
            BigDecimal value = BigDecimal.valueOf(100);
            BigDecimal divide = valueOfProfitPercentage.divide(value);
            BigDecimal result = divide.multiply(subNetPrice);
            BigDecimal totalSellingPrice = subNetPrice.add(result).setScale(2, RoundingMode.HALF_UP);
            String price = totalSellingPrice.toString();
            sellingPriceList[0] = price;

        }
        return sellingPriceList[0];
    }

    private void generateBarCode() {
        try {
            if (!txtItemCode.getText().matches("\\d{12}")) return;
            Barcode barcode = BarcodeFactory.createEAN13(txtItemCode.getText());
            barcode.setFont(new Font("Ubuntu", Font.PLAIN, 16));
            BufferedImage barcodeImage = BarcodeImageHandler.getImage(barcode);
            WritableImage fxImage = SwingFXUtils.toFXImage(barcodeImage, null);
            imgBarcode.setImage(fxImage);


        } catch (BarcodeException e) {
            throw new RuntimeException(e);
        } catch (OutputException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnAddBrandOnAction(ActionEvent actionEvent) throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/BrandNames.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void btnAddModelOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/ListOfBikes.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void btnAddCategoryOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/PartsCategory.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void btnBatchNumberOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/NewBatchScene.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void btnClearAllOnAction(ActionEvent actionEvent) {
        clearAll();
    }
}