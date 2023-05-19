package lk.ijse.dep10.possystem.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.Printer;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.Bill;
import lk.ijse.dep10.possystem.model.BillDescription;
import lk.ijse.dep10.possystem.model.Item;
import lk.ijse.dep10.possystem.model.User;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class BillingSceneController {

    public AnchorPane root;
    public Button btnDelete;
    public Button btnDescription;
    public TableView<Bill> tblBills;
    public Button btnAddCustomer;
    public TableView<Item> tblPrintBill;
    public TextField txtSearchBills;
    public Button btnAddItem;
    public Button btnDeleteRow;
    public Button btnPrintBill;
    public ComboBox<String> cmbSearchCustomer;
    public ComboBox<String> cmbSearchItems;
    public Button btnTotalPrice;
    @FXML
    private Button btnEnter;
    @FXML
    private Button btnNewBill;
    @FXML
    private TextField txtBalance;
    @FXML
    private TextField txtBillNumber;
    @FXML
    private TextField txtCash;
    @FXML
    private TextField txtCashier;
    @FXML
    private TextField txtDateTime;
    @FXML
    private TextField txtTotalPrice;
    private JasperReport jasperReport;


    public void initialize() {
        cmbSearchItems.requestFocus();

        tblBills.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        tblBills.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        tblBills.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("cashierName"));
        tblBills.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        tblBills.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("cash"));
        tblBills.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("balance"));

        loadAllItems();

        tblPrintBill.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblPrintBill.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        tblPrintBill.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        tblPrintBill.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("price"));
        tblPrintBill.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("consumedQty"));

        TableColumn colQty = tblPrintBill.getColumns().get(3);
        colQty.setCellFactory(TextFieldTableCell.forTableColumn());

        tblPrintBill.setEditable(true);
        colQty.setEditable(true);
        colQty.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent cellEditEvent) {
                tblPrintBill.getSelectionModel().getSelectedItem().setConsumedQty(cellEditEvent.getNewValue().toString());
                tblPrintBill.refresh();

            }
        });

        txtCash.textProperty().addListener((ov, pv, current) -> {

            try {
                if (!current.isEmpty()) {
                    BigDecimal bigDecimalCash = new BigDecimal(current);
                    String totalPrice = txtTotalPrice.getText().strip();
                    BigDecimal bigDecimalTotalPrice = new BigDecimal(totalPrice);
                    txtBalance.setText(bigDecimalCash.subtract(bigDecimalTotalPrice).toString());
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        });

        searchBills();
        putDataToComboBoxes();
        addDataToTblPrintBill();
        tblBillSelection();
        initializeJasperReport();

        btnDeleteRow.setDisable(true);
        btnDelete.setDisable(true);
        btnEnter.setDisable(true);
        btnPrintBill.setDisable(true);
        btnTotalPrice.setDisable(true);
        cmbSearchCustomer.setDisable(true);
        cmbSearchItems.setDisable(true);

    }

    private void initializeJasperReport() {
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(getClass().getResourceAsStream("/report/bill.jrxml"));
            jasperReport = JasperCompileManager.compileReport(jasperDesign);

        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private void putDataToComboBoxes() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT name FROM Customer";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            ObservableList<String> customerList = cmbSearchCustomer.getItems();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                customerList.add(name);
            }

            TextFields.bindAutoCompletion(cmbSearchCustomer.getEditor(), cmbSearchCustomer.getItems());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql1 = "SELECT item_code FROM Items";
            String sql2 = "SELECT item_name FROM Items";
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            PreparedStatement statement2 = connection.prepareStatement(sql2);
            ResultSet resultSet1 = statement1.executeQuery();
            ResultSet resultSet2 = statement2.executeQuery();

            ObservableList<String> itemCodeList = cmbSearchItems.getItems();
            while (resultSet1.next()) {
                String itemCode = resultSet1.getString("item_code");
                itemCodeList.add(itemCode);
            }

            ObservableList<String> itemNameList = cmbSearchItems.getItems();
            while (resultSet2.next()) {
                String itemName = resultSet2.getString("item_name");
                itemNameList.add(itemName);
            }
            TextFields.bindAutoCompletion(cmbSearchItems.getEditor(), cmbSearchItems.getItems());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private void addDataToTblPrintBill (){
        cmbSearchItems.valueProperty().addListener((ov, previous, current) -> {

            if (!(cmbSearchItems.getValue() == null)) {
                Connection connection = DBConnection.getInstance().getConnection();
                try {
                    Statement stm = connection.createStatement();
                    String sql = "SELECT  * FROM Items WHERE item_code LIKE  '%1$s' OR  " +
                            "item_name LIKE '%1$s' ";

                    sql = String.format(sql, "%" + current + "%");

                    ResultSet rst = stm.executeQuery(sql);

                    ObservableList<Item> itemList = tblPrintBill.getItems();


                    while (rst.next()) {
                        Long itemCode = rst.getLong("item_code");
                        String itemName = rst.getString("item_name");
                        BigDecimal unitPrice = rst.getBigDecimal("selling_price");

                        itemList.add(new Item(itemCode, itemName, unitPrice));

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void searchBills() {
        txtSearchBills.textProperty().addListener((ov, previous, current) -> {

            Connection connection = DBConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                Statement stm2 = connection.createStatement();
                String sql = "SELECT  * FROM  Bills WHERE number LIKE  '%1$s' ";

                sql = String.format(sql, "%" + current + "%");
                ResultSet rst = stm.executeQuery(sql);

                ObservableList<Bill> billList = tblBills.getItems();
                billList.clear();

                while (rst.next()) {
                    int number = rst.getInt("number");
                    LocalDateTime dateTime = rst.getTimestamp("date_time").toLocalDateTime();
                    String cashierName = rst.getString("cashier_name");
                    BigDecimal totalPrice = rst.getBigDecimal("total_price");
                    BigDecimal cash = rst.getBigDecimal("cash");
                    BigDecimal balance = rst.getBigDecimal("balance");
                    ArrayList<BillDescription> billDescriptionList = new ArrayList<>();

                    String sql2 = "SELECT  * FROM  BillDescription WHERE bill_number LIKE  " + number + " ";
                    ResultSet resultSetBillDescription = stm2.executeQuery(sql2);

                    while (resultSetBillDescription.next()) {
                        int billNumber = resultSetBillDescription.getInt("bill_number");
                        Long itemCode = resultSetBillDescription.getLong("item_code");
                        String item = resultSetBillDescription.getString("item");
                        BigDecimal unitPrice = resultSetBillDescription.getBigDecimal("unit_price");
                        int quantity = resultSetBillDescription.getInt("quantity");
                        BigDecimal price = resultSetBillDescription.getBigDecimal("price");


                        billDescriptionList.add(new BillDescription(billNumber, itemCode, item, unitPrice, quantity, price));

                    }

                    billList.add(new Bill(number, dateTime, cashierName, totalPrice, cash, balance, billDescriptionList));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private void tblBillSelection() {
        tblBills.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            tblBills.refresh();
            if (current == null) return;

            ObservableList<Item> observableList = tblPrintBill.getItems();
            observableList.clear();

            txtBillNumber.setText(String.valueOf(current.getBillNumber()));
            txtDateTime.setText(String.valueOf(current.getDateTime()));
            txtCashier.setText(current.getCashierName());
            txtTotalPrice.setText(String.valueOf(current.getTotalPrice()));
            txtCash.setText(String.valueOf(current.getCash()));
            txtBalance.setText(String.valueOf(current.getBalance()));

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement stmp = connection.prepareStatement("SELECT *FROM Loyalty WHERE bill_number=?");
                stmp.setInt(1, current.getBillNumber());
                ResultSet resultSet = stmp.executeQuery();
                while (resultSet.next()) {
                    String customerName = resultSet.getString("customer_name");
                    cmbSearchCustomer.setValue(customerName);

                }

                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM BillDescription WHERE bill_number=?");
                preparedStatement.setInt(1, current.getBillNumber());
                ResultSet resultSet2 = preparedStatement.executeQuery();

                while (resultSet2.next()) {
                    Long itemCode = resultSet2.getLong("item_code");
                    String itemName = resultSet2.getString("item");
                    BigDecimal unitPrice = resultSet2.getBigDecimal("unit_price");
                    int consumedQty = resultSet2.getInt("quantity");
                    BigDecimal price = resultSet2.getBigDecimal("price");
                    observableList.add(new Item(itemCode, itemName, unitPrice, consumedQty + "", price));
                    tblPrintBill.refresh();

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private void loadAllItems() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();

            ResultSet resultSetBill = stm.executeQuery("SELECT * FROM Bills");
            PreparedStatement stm2 = connection.prepareStatement("SELECT *FROM BillDescription WHERE bill_number=?");

            while (resultSetBill.next()) {
                int number = resultSetBill.getInt("number");
                LocalDateTime dateTime = resultSetBill.getTimestamp("date_time").toLocalDateTime();
                String cashierName = resultSetBill.getString("cashier_name");
                BigDecimal totalPrice = resultSetBill.getBigDecimal("total_price");
                BigDecimal cash = resultSetBill.getBigDecimal("cash");
                BigDecimal balance = resultSetBill.getBigDecimal("balance");
                ArrayList<BillDescription> billDescriptionList = new ArrayList<>();


                stm2.setInt(1, number);
                ResultSet resultSetBillDescription = stm2.executeQuery();

                while (resultSetBillDescription.next()) {
                    int billNumber = resultSetBillDescription.getInt("bill_number");
                    Long itemCode = resultSetBillDescription.getLong("item_code");
                    String item = resultSetBillDescription.getString("item");
                    BigDecimal unitPrice = resultSetBillDescription.getBigDecimal("unit_price");
                    int quantity = resultSetBillDescription.getInt("quantity");
                    BigDecimal price = resultSetBillDescription.getBigDecimal("price");


                    billDescriptionList.add(new BillDescription(billNumber, itemCode, item, unitPrice, quantity, price));

                }
                Bill bill = new Bill(number, dateTime, cashierName, totalPrice, cash, balance, billDescriptionList);
                tblBills.getItems().add(bill);

            }


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load bill");
            Platform.exit();
        }
    }

    @FXML
    void btnNewBillOnAction(ActionEvent event) {
        txtBillNumber.clear();
        txtDateTime.clear();
        txtCashier.clear();
        if (!(tblBills.getItems().isEmpty())) {
            tblPrintBill.getItems().clear();
        }
        txtTotalPrice.clear();
        txtCash.clear();
        txtBalance.clear();
        txtSearchBills.clear();
        tblBills.getSelectionModel().clearSelection();

        if (!(cmbSearchItems.getSelectionModel().isEmpty())) {
            cmbSearchItems.getSelectionModel().clearSelection();
        }
        if (!(cmbSearchCustomer.getSelectionModel().isEmpty())) {
            cmbSearchCustomer.getSelectionModel().clearSelection();
        }

        billNumberGenerator();

        txtDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        User principal = (User) System.getProperties().get("principal");
        txtCashier.setText(String.format("%s: %s", principal.getRole().name(), principal.getFullName()));

        cmbSearchCustomer.requestFocus();
        cmbSearchItems.requestFocus();

        btnDeleteRow.setDisable(false);
        btnDelete.setDisable(false);
        btnEnter.setDisable(false);
        btnPrintBill.setDisable(false);
        btnTotalPrice.setDisable(false);
        cmbSearchCustomer.setDisable(false);
        cmbSearchItems.setDisable(false);

    }

    private void billNumberGenerator() {
        ObservableList<Bill> billDetails = tblBills.getItems();
        if (billDetails.isEmpty()) {
            txtBillNumber.setText(String.valueOf(1));
        } else {
            int id = tblBills.getItems().get(billDetails.size() - 1).getBillNumber();
            int newId = id + 1;
            txtBillNumber.setText(String.valueOf(newId));
        }

    }


    @FXML
    void btnEnterOnAction(ActionEvent event) {
        if (tblBills.getSelectionModel().getSelectedItem() == null) {
            int billNumber = Integer.parseInt(txtBillNumber.getText());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(txtDateTime.getText(), formatter);
            String cashierName = txtCashier.getText();
            BigDecimal totalPrice = new BigDecimal(txtTotalPrice.getText());
            BigDecimal cash = new BigDecimal(txtCash.getText());
            BigDecimal balance = new BigDecimal(txtBalance.getText());


            Connection connection = DBConnection.getInstance().getConnection();
            try {
                Bill selectedBill = tblBills.getSelectionModel().getSelectedItem();

                if (selectedBill == null) {

                    connection.setAutoCommit(false);
                    PreparedStatement stm = connection.prepareStatement("INSERT INTO Bills(number, date_time,cashier_name,total_price,cash,balance) VALUES (?,?,?,?,?,?)");

                    stm.setInt(1, billNumber);
                    stm.setTimestamp(2, Timestamp.valueOf(dateTime));
                    stm.setString(3, cashierName);
                    stm.setBigDecimal(4, totalPrice);
                    stm.setBigDecimal(5, cash);
                    stm.setBigDecimal(6, balance);
                    stm.executeUpdate();

                    PreparedStatement stm2 = connection.prepareStatement("INSERT INTO BillDescription(bill_number, item_code,item,unit_price,quantity,price) VALUES (?,?,?,?,?,?)");

                    ObservableList<Item> boughtObservableList2 = tblPrintBill.getItems();
                    for (Item item : boughtObservableList2) {
                        int billNumber2 = Integer.parseInt(txtBillNumber.getText());
                        Long itemCode2 = item.getItemCode();
                        String item2 = item.getItemName();
                        BigDecimal unitPrice2 = item.getSellingPrice();
                        BigDecimal price2 = item.getPrice();
                        int qty2 = price2.divide(unitPrice2).intValue();

                        stm2.setInt(1, billNumber2);
                        stm2.setLong(2, itemCode2);
                        stm2.setString(3, item2);
                        stm2.setBigDecimal(4, unitPrice2);
                        stm2.setInt(5, qty2);
                        stm2.setBigDecimal(6, price2);
                        stm2.executeUpdate();


                        Statement stmX = connection.createStatement();
                        String sql = "UPDATE Items SET qty = qty - " + qty2 + " WHERE item_code = " + itemCode2;
                        stmX.executeUpdate(sql);

                    }
                    if (cmbSearchCustomer.getValue() != null) {
                        PreparedStatement stm3 = connection.prepareStatement("INSERT INTO Loyalty(customer_name, bill_number,bill_date,bill_value) VALUES (?,?,?,?)");
                        stm3.setString(1, cmbSearchCustomer.getValue());
                        stm3.setInt(2, billNumber);
                        stm3.setTimestamp(3, Timestamp.valueOf(dateTime));
                        stm3.setBigDecimal(4, totalPrice);
                        stm3.executeUpdate();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Please enter the customer").showAndWait();
                    }
                }
                connection.commit();
            } catch (Throwable e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                System.out.println("failed to save bill");
            } finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            ObservableList<Item> boughtObservableList = tblPrintBill.getItems();
            ArrayList<BillDescription> boughtArrayList = new ArrayList<>();
            for (Item item : boughtObservableList) {
                int billNumber1 = Integer.parseInt(txtBillNumber.getText());
                Long itemCode = item.getItemCode();
                String item1 = item.getItemName();
                BigDecimal unitPrice = item.getSellingPrice();
                int qty = item.getQty();
                BigDecimal price = item.getPrice();

                BillDescription billDescription = new BillDescription(billNumber1, itemCode, item1, unitPrice, qty, price);
                boughtArrayList.add(billDescription);
            }

            Bill newBill = new Bill(billNumber, dateTime, cashierName, totalPrice, cash, balance, boughtArrayList);
            tblBills.getItems().add(newBill);


        } else {
            /*update bill table*/
            int billNumber = Integer.parseInt(txtBillNumber.getText());
            Timestamp dateTime = Timestamp.valueOf(LocalDateTime.parse(txtDateTime.getText().replace("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            String cashierName = txtCashier.getText();
            BigDecimal totalPrice = new BigDecimal(txtTotalPrice.getText());
            BigDecimal cash = new BigDecimal(txtCash.getText());
            BigDecimal balance = new BigDecimal(txtBalance.getText());
            ArrayList<BillDescription> boughtArrayList = new ArrayList<>();

            if (tblBills.getSelectionModel().getSelectedItem() != null) {

                try {
                    Connection connection = DBConnection.getInstance().getConnection();

                    /*update the loyalty table*/
                    PreparedStatement ps3 = connection.prepareStatement("UPDATE Loyalty SET bill_value=? WHERE bill_number=?");
                    ps3.setBigDecimal(1, totalPrice);
                    ps3.setInt(2, billNumber);
                    ps3.executeUpdate();


                    ObservableList<Item> boughtObservableList2 = tblPrintBill.getItems();

                    for (Item item : boughtObservableList2) {

                        String item2 = item.getItemName();
                        BigDecimal unitPrice2 = item.getSellingPrice();
                        int qty2 = Integer.parseInt(item.getConsumedQty());
                        BigDecimal price2 = item.getPrice();
                        int billNumber2 = Integer.parseInt(txtBillNumber.getText());
                        Long itemCode2 = item.getItemCode();

                        /*get the old consumed quantity*/
                        PreparedStatement stmt = connection.prepareStatement("SELECT quantity FROM BillDescription WHERE item_code = ?");
                        stmt.setLong(1, itemCode2);
                        ResultSet rs = stmt.executeQuery();
                        int oldQuantity = 0;
                        if (rs.next()) {
                            oldQuantity = rs.getInt("quantity");
                        }
                        /*update item table quantity, first add old quantity to the item table then subtract new
                         * quantity from item table*/
                        PreparedStatement stmX = connection.prepareStatement("UPDATE Items SET qty = qty+" + oldQuantity + " WHERE item_code =? ");
                        stmX.setLong(1, itemCode2);
                        stmX.executeUpdate();

                        PreparedStatement stmY = connection.prepareStatement("UPDATE Items SET qty = qty-" + qty2 + " WHERE item_code =? ");
                        stmY.setLong(1, itemCode2);
                        stmY.executeUpdate();

                        /*check if the items already exist in the billDescription table*/
                        PreparedStatement stmZ = connection.prepareStatement("SELECT * FROM BillDescription WHERE bill_number = ? AND item_code = ?");
                        stmZ.setInt(1, billNumber2);
                        stmZ.setLong(2, itemCode2);
                        ResultSet rstZ = stmZ.executeQuery();

                        if (rstZ.next()) {
                            /*if item exist in the Bill Description table update that values*/

                            PreparedStatement stm2 = connection.prepareStatement("UPDATE BillDescription SET item=?,unit_price=?,quantity=?,price=? WHERE bill_number=? AND item_code=?");
                            stm2.setString(1, item2);
                            stm2.setBigDecimal(2, unitPrice2);
                            stm2.setInt(3, qty2);
                            stm2.setBigDecimal(4, price2);
                            stm2.setInt(5, billNumber2);
                            stm2.setLong(6, itemCode2);
                            stm2.executeUpdate();
                        } else {
                            /*add new item to the billDescription table*/
                            PreparedStatement stmV = connection.prepareStatement("INSERT INTO BillDescription (bill_number, item_code, item, unit_price, quantity, price) VALUES (?, ?, ?, ?, ?, ?)");
                            stmV.setInt(1, billNumber2);
                            stmV.setLong(2, itemCode2);
                            stmV.setString(3, item2);
                            stmV.setBigDecimal(4, unitPrice2);
                            stmV.setInt(5, qty2);
                            stmV.setBigDecimal(6, price2);
                            stmV.executeUpdate();
                        }


                        BillDescription billDescription = new BillDescription(billNumber2, itemCode2, item2, unitPrice2, qty2, price2);
                        boughtArrayList.add(billDescription);


                    }

                    /*finally update the bill table*/
                    PreparedStatement ps1 = connection.prepareStatement("UPDATE Bills SET date_time=?,cashier_name=?,total_price=?,cash=?,balance=? WHERE number=?");

                    ps1.setTimestamp(1, dateTime);
                    ps1.setString(2, cashierName);
                    ps1.setBigDecimal(3, totalPrice);
                    ps1.setBigDecimal(4, cash);
                    ps1.setBigDecimal(5, balance);
                    ps1.setInt(6, billNumber);
                    ps1.executeUpdate();


                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                /*update the value of javafx table*/
                Bill newBill = new Bill(billNumber, dateTime.toLocalDateTime(), cashierName, totalPrice, cash, balance, boughtArrayList);
                int selectedBillIndex = tblBills.getSelectionModel().getSelectedIndex();
                tblBills.getItems().set(selectedBillIndex, newBill);


            }
        }

    }


    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm2 = connection.createStatement();
            String sql2 = "DELETE FROM BillDescription WHERE bill_number=%d";
            sql2 = String.format(sql2, tblBills.getSelectionModel().getSelectedItem().getBillNumber());
            stm2.executeUpdate(sql2);

            Statement stm3 = connection.createStatement();
            String sql3 = "DELETE FROM Loyalty WHERE bill_number=%d";
            sql3 = String.format(sql3, tblBills.getSelectionModel().getSelectedItem().getBillNumber());
            stm3.executeUpdate(sql3);

            Statement stm = connection.createStatement();
            String sql = "DELETE FROM Bills WHERE number=%d";
            sql = String.format(sql, tblBills.getSelectionModel().getSelectedItem().getBillNumber());
            stm.executeUpdate(sql);


            ArrayList<BillDescription> billDescription = tblBills.getSelectionModel().getSelectedItem().getBillDescription();
            for (BillDescription description : billDescription) {
                Statement stmY = connection.createStatement();
                String sqlY = "UPDATE Items SET qty = qty + " + description.getQuantity() + " WHERE item_code = " + description.getItemCode();
                stmY.executeUpdate(sqlY);
            }

            tblBills.getItems().remove(tblBills.getSelectionModel().getSelectedItem());


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("fail to delete bill");
        }

    }


    public void btnDescriptionOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/BillDescription.fxml")).load()));
        stage.setTitle("Bills");
        stage.centerOnScreen();
        stage.show();
    }

    public void btnAddCustomerOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/CustomerView.fxml")).load()));
        stage.setTitle("Add Customer");
        stage.setMaximized(true);
        stage.centerOnScreen();
        stage.show();
    }

    public void btnAddItemOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/view/ItemScene.fxml")).load()));
        stage.setTitle("Add Item");
        stage.setMaximized(true);
        stage.centerOnScreen();
        stage.show();
    }


    public void btnDeleteRowOnAction(ActionEvent actionEvent) {


        if (tblBills.getSelectionModel().getSelectedItem() != null &&
                tblPrintBill.getSelectionModel().getSelectedItem() != null) {

            Item selectedItem = tblPrintBill.getSelectionModel().getSelectedItem();

            try {
                Connection connection = DBConnection.getInstance().getConnection();

                Long itemCode = selectedItem.getItemCode();
                int qty = Integer.parseInt(selectedItem.getConsumedQty());

                PreparedStatement stmX = connection.prepareStatement("UPDATE Items SET qty = qty + ? WHERE item_code = ?");
                stmX.setInt(1, qty);
                stmX.setLong(2, itemCode);
                stmX.executeUpdate();

                PreparedStatement stmY = connection.prepareStatement("DELETE FROM BillDescription WHERE bill_number = ? AND item_code = ?");
                stmY.setInt(1, Integer.parseInt(txtBillNumber.getText()));
                stmY.setLong(2, itemCode);
                stmY.executeUpdate();


                tblPrintBill.getItems().remove(selectedItem);


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        } else {
            tblPrintBill.getItems().remove(tblPrintBill.getSelectionModel().getSelectedItem());
        }

    }

    public void btnPrintBillOnAction(ActionEvent actionEvent) {

        ObservableList<Item> itemList = tblPrintBill.getItems();
        Item[] items = itemList.toArray(new Item[0]);
        HashMap<String, Object> reportParams = new HashMap<>();

        reportParams.put("customerName", cmbSearchCustomer.getValue());
        reportParams.put("billNumber", txtBillNumber.getText());
        reportParams.put("dateAndTime", txtDateTime.getText());
        reportParams.put("total", txtTotalPrice.getText());
        reportParams.put("cash", txtCash.getText());
        reportParams.put("balance", txtBalance.getText());
        reportParams.put("cashierName", txtCashier.getText());

        try {
            JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(items);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams, dataSource);
            JasperViewer.viewReport(jasperPrint, false);

            Printer defaultPrinter = Printer.getDefaultPrinter();
            if (defaultPrinter == null) {
                new Alert(Alert.AlertType.ERROR, "no printer has been configured").showAndWait();
                return;
            }

            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {
            e.printStackTrace();
        }


    }

    public void btnTotalPriceOnAction(ActionEvent actionEvent) {

        BigDecimal total = BigDecimal.ZERO;

        for (Item item : tblPrintBill.getItems()) {

            try {
                BigDecimal price = item.getPrice();
                total = total.add(price);
            } catch (NullPointerException | NumberFormatException e) {
                System.err.println("Error calculating total price: " + e.getMessage());
            }
        }

        txtTotalPrice.setText(total.toString());


    }
}

