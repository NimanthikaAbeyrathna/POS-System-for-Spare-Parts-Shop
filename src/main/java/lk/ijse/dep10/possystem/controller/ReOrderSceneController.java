package lk.ijse.dep10.possystem.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.BillDescription;
import lk.ijse.dep10.possystem.model.Item;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class ReOrderSceneController {

    @FXML
    private TableView<Item> tblReOrderItem;

    @FXML
    private TextField txtSearch;

    public void initialize(){
        tblReOrderItem.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblReOrderItem.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        tblReOrderItem.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("brandName"));
        tblReOrderItem.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("partsCategory"));
        tblReOrderItem.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("model"));
        tblReOrderItem.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblReOrderItem.getColumns().get(6).setCellValueFactory(new PropertyValueFactory<>("dateOfBought"));

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();

            ResultSet resultSetBill = stm.executeQuery("SELECT * FROM Items WHERE qty < 6");

            while (resultSetBill.next()) {
                long itemCode = resultSetBill.getLong("item_code");
                String itemName = resultSetBill.getString("item_name");
                String brandName = resultSetBill.getString("brand_name");
                String partsCategory = resultSetBill.getString("parts_category");
                String model = resultSetBill.getString("model");
                int qty = resultSetBill.getInt("qty");
                Date dateOfBought = resultSetBill.getDate("date_bought");

                if (qty < 6) {
                    Item item = new Item(itemCode, itemName, brandName, partsCategory, model, qty, dateOfBought);
                    tblReOrderItem.getItems().add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load reOrder items");
            Platform.exit();
        }



        txtSearch.textProperty().addListener((ov, previous, current) -> {

            Connection connection = DBConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT  * FROM  Items WHERE item_code LIKE  '%1$s' OR item_name LIKE '%2$s' ";

                sql = String.format(sql, "%" + current + "%", "%" + current + "%");

                ResultSet rst = stm.executeQuery(sql);

                ObservableList<Item> reOrderItemItemsList = tblReOrderItem.getItems();
                reOrderItemItemsList.clear();

                while (rst.next()) {
                    long itemCode = rst.getLong("item_code");
                    String itemName = rst.getString("item_name");
                    String brandName = rst.getString("brand_name");
                    String partsCategory = rst.getString("parts_category");
                    String model = rst.getString("model");
                    int qty = rst.getInt("qty");
                    Date dateOfBought = rst.getDate("date_bought");

                    if (qty < 6) {
                        reOrderItemItemsList.add(new Item(itemCode, itemName, brandName, partsCategory, model, qty, dateOfBought));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }

}
