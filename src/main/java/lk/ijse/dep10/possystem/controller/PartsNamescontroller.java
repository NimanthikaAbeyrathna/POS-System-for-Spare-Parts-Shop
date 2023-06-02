package lk.ijse.dep10.possystem.controller;

import javafx.beans.binding.When;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.PartsNames;

import java.sql.*;
import java.util.ArrayList;

public class PartsNamescontroller {

    public ComboBox cmbCategory;
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnRemove;

    @FXML
    private TableView<PartsNames> tblItems;

    @FXML
    private TextField txtInput;


    public void initialize() {
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemName"));
        cmbCategory.getItems().addAll(getCategoryFromDataBase());

        setValuesFromTable();
        cmbCategory.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            if (current != null || previous != null) {
                tblItems.getItems().clear();
                loadDataFromDataBase();
            }

        });

    }


    private void setValuesFromTable() {
        tblItems.getSelectionModel().selectedItemProperty().addListener((observableValue, partsNames, current) -> {
            if (current != null) {
                String itemName = current.getItemName();
                txtInput.setText(itemName);
            }
        });
    }

    private ArrayList<String> getCategoryFromDataBase() {
        ArrayList<String> categoryList = new ArrayList<>();
        String sql = "SELECT *FROM Parts_Category";

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String categoryName = rst.getString(1);
                categoryList.add(categoryName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categoryList;
    }

    private void loadDataFromDataBase() {


        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT *FROM Parts WHERE parts_category=?";

        Object selectedItem = cmbCategory.getSelectionModel().getSelectedItem();

        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, selectedItem.toString());
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String itemName = rst.getString(2);
                PartsNames partsNames = new PartsNames(itemName);
                tblItems.getItems().add(partsNames);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean dataValidation() {
        boolean dataValid = true;
        String itemName = txtInput.getText();
        if (!(itemName.matches("[A-Za-z ]+"))) {
            txtInput.clear();
            txtInput.requestFocus();
            Alert alert = new Alert(Alert.AlertType.ERROR, "please add correct name");
            alert.showAndWait();
            dataValid = false;
        }
        Object selectedItem = cmbCategory.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "please select category");
            alert.showAndWait();
            txtInput.clear();
            txtInput.requestFocus();
            dataValid = false;
        }
        return dataValid;
    }


    @FXML
    void btnAddOnAction(ActionEvent event) {

        if (!(dataValidation())) {

            return;
        }
        String item = txtInput.getText();
        PartsNames nameOfItem = new PartsNames(item);
        Object selectedItem = cmbCategory.getSelectionModel().getSelectedItem();

        PartsNames selectedItem2 = tblItems.getSelectionModel().getSelectedItem();

        if (selectedItem2 == null) {

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                String sql = "INSERT INTO Parts (parts_category, parts_type) VALUES (?,?)";
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setString(1, selectedItem.toString());
                prd.setString(2, item);
                tblItems.getItems().add(nameOfItem);
                txtInput.clear();
                prd.executeUpdate();
                connection.commit();
            } catch (Throwable e) {
                try {
                    DBConnection.getInstance().getConnection().rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save the part name, try again!").show();
            } finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {

            try {
                Connection connection1 = DBConnection.getInstance().getConnection();
                connection1.setAutoCommit(false);

                String sqlUpdate = "UPDATE Parts SET parts_category=? , parts_type=? WHERE parts_type=?";
                PreparedStatement prd1 = connection1.prepareStatement(sqlUpdate);
                prd1.setString(1, selectedItem.toString());
                prd1.setString(2, item);
                prd1.setString(3, selectedItem2.getItemName());
                tblItems.getItems().add(nameOfItem);
                txtInput.clear();
                prd1.executeUpdate();
                connection1.commit();

            } catch (Throwable e) {
                try {
                    DBConnection.getInstance().getConnection().rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to update the part name, try again!").show();
            } finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {

        PartsNames selectedItem = tblItems.getSelectionModel().getSelectedItem();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            String sql = "DELETE FROM Parts WHERE parts_type=?";
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, selectedItem.getItemName());
            tblItems.getItems().remove(selectedItem);
            prd.executeUpdate();
            connection.commit();
        } catch (Throwable e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the part name, try again!").show();
        } finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
