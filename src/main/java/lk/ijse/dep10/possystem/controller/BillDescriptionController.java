package lk.ijse.dep10.possystem.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep10.possystem.db.DbConnection;
import lk.ijse.dep10.possystem.model.BillDescription;
import java.math.BigDecimal;
import java.sql.*;



public class BillDescriptionController {

    public AnchorPane root;
    @FXML
    private TableView<BillDescription> tblBillDescription;

    @FXML
    private TextField txtSearchBar;

    public void initialize() {
        tblBillDescription.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        tblBillDescription.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblBillDescription.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("item"));
        tblBillDescription.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblBillDescription.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblBillDescription.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("price"));


        try {
            Connection connection = DbConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();

            ResultSet resultSetBill = stm.executeQuery("SELECT * FROM BillDescription");

            while (resultSetBill.next()) {
                int billNumber = resultSetBill.getInt("bill_number");
                Long itemCode = resultSetBill.getLong("item_code");
                String item = resultSetBill.getString("item");
                BigDecimal unitPrice = resultSetBill.getBigDecimal("unit_price");
                int quantity = resultSetBill.getInt("quantity");
                BigDecimal price = resultSetBill.getBigDecimal("price");

                BillDescription billDescription = new BillDescription(billNumber,itemCode, item, unitPrice, quantity, price);
                tblBillDescription.getItems().add(billDescription);

            }


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load billDescription");
            Platform.exit();
        }

        txtSearchBar.textProperty().addListener((ov, previous, current) -> {

            Connection connection = DbConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT  * FROM  BillDescription WHERE bill_number LIKE  '%1$s' ";

                sql = String.format(sql, "%" + current + "%");

                ResultSet rst = stm.executeQuery(sql);

                ObservableList<BillDescription> billDescriptionList = tblBillDescription.getItems();
                billDescriptionList.clear();

                while (rst.next()) {
                    int billNumber = rst.getInt("bill_number");
                    Long itemCode = rst.getLong("item_code");
                    String item = rst.getString("item");
                    BigDecimal unitPrice = rst.getBigDecimal("unit_price");
                    int quantity = rst.getInt("quantity");
                    BigDecimal price = rst.getBigDecimal("price");


                    billDescriptionList.add(new BillDescription(billNumber,itemCode,item, unitPrice,quantity,price));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

    }

}
