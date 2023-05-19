package lk.ijse.dep10.possystem.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep10.possystem.db.DbConnection;
import lk.ijse.dep10.possystem.model.Loyalty;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;


public class LoyaltySceneController {

    public AnchorPane root;
    public TableView<Loyalty> tblLoyalty;
    @FXML
    private TextField txtSearchField;

    public void initialize() {
        tblLoyalty.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tblLoyalty.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("billNumber"));
        tblLoyalty.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("billDate"));
        tblLoyalty.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("billValue"));

        try {
            Connection connection = DbConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery("SELECT * FROM Loyalty");

            while (resultSet.next()) {
                String customerName = resultSet.getString("customer_name");
                int billNumber = resultSet.getInt("bill_number");
                LocalDateTime billDate = resultSet.getTimestamp("bill_date").toLocalDateTime();
                BigDecimal billValue = resultSet.getBigDecimal("bill_value");

                Loyalty loyalty = new Loyalty(customerName, billNumber, billDate, billValue);
                tblLoyalty.getItems().add(loyalty);

            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load loyalty");
            Platform.exit();
        }

        txtSearchField.textProperty().addListener((ov, previous, current) -> {

            Connection connection = DbConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT  * FROM  Loyalty WHERE customer_name LIKE  '%1$s' OR  " +
                        "bill_number LIKE '%1$s' ";

                sql = String.format(sql, "%" + current + "%");
                ResultSet rst = stm.executeQuery(sql);

                ObservableList<Loyalty> loyaltyList = tblLoyalty.getItems();
                loyaltyList.clear();

                while (rst.next()) {
                    String customerName = rst.getString("customer_name");
                    int billNumber = rst.getInt("bill_number");
                    LocalDateTime billDate = rst.getTimestamp("bill_date").toLocalDateTime();
                    BigDecimal billValue = rst.getBigDecimal("bill_value");

                    loyaltyList.add(new Loyalty(customerName, billNumber, billDate, billValue));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
    }


}
