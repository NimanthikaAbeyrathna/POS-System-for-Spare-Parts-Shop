package lk.ijse.dep10.possystem.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.BrandNames;

import java.sql.*;

public class BrandNamescontroller {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView<BrandNames> tblBrand;

    @FXML
    private TextField txtInput;

    public void initialize() {
        tblBrand.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("brandName"));
        loadDataFromTable();
        loadDataFromDataBase();

    }

    private boolean validationOfInput() {
        boolean inputValidation = true;
        ObservableList<BrandNames> items = tblBrand.getItems();
        for (BrandNames item : items) {
            if (txtInput.getText().equals(item.getBrandName())) {
                System.out.println("if");
                Alert alert = new Alert(Alert.AlertType.ERROR, "This item already exist enter new one");
                alert.showAndWait();
                txtInput.clear();
                inputValidation = false;
            }
        }
        return inputValidation;
    }


    private boolean dataValidation() {
        boolean dataValidate = true;
        if (!(txtInput.getText().matches("^[A-Z][a-z]+"))) {
            txtInput.requestFocus();
            txtInput.clear();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter the Bike Name in Correct Format");
            alert.showAndWait();
            dataValidate = false;
        }
        return dataValidate;
    }

    private void loadDataFromTable() {
        tblBrand.getSelectionModel().selectedItemProperty().addListener((observableValue, brandNames, current) -> {
            if (current != null) {
                BrandNames selectedItem = tblBrand.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String brandName = selectedItem.getBrandName();
                    txtInput.setText(brandName);
                    txtInput.requestFocus();
                }
            }
        });
    }

    private void loadDataFromDataBase() {

        String sql = "SELECT brand_name FROM Brands ";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String brandNames = rst.getString(1);
                BrandNames brandNames1 = new BrandNames(brandNames);
                tblBrand.getItems().add(brandNames1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!(dataValidation() && validationOfInput())) {
            return;
        }

        String input = txtInput.getText();
        BrandNames brandNames1 = new BrandNames(input);

        validationOfInput();

        BrandNames selectedItem1 = tblBrand.getSelectionModel().getSelectedItem();

        if (selectedItem1 == null) {

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                String sql = "INSERT INTO Brands (brand_name) VALUES (?)";
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setString(1, input);
                tblBrand.getItems().add(brandNames1);
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
                new Alert(Alert.AlertType.ERROR, "Failed to save the brand name, try again!").show();
            }finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            System.out.println("update");
            String sqlUpdate = "UPDATE Brands SET brand_name =? WHERE brand_name=?";
            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                PreparedStatement prd = connection.prepareStatement(sqlUpdate);
                prd.setString(1, input);
                prd.setString(2, selectedItem1.getBrandName());
                tblBrand.getItems().remove(selectedItem1);
                tblBrand.getItems().add(brandNames1);
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
                new Alert(Alert.AlertType.ERROR, "Failed to update the brand name, try again!").show();
            }finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        int selectedIndex = tblBrand.getSelectionModel().getSelectedIndex();
        BrandNames selectedItem = tblBrand.getSelectionModel().getSelectedItem();
        String brandName = selectedItem.getBrandName();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stm = connection.prepareStatement("DELETE  FROM List_Of_Bikes WHERE brand_name=?");
            stm.setString(1,brandName);
            stm.executeUpdate();

            String sql = "DELETE FROM Brands WHERE brand_name=?";
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, brandName);
            prd.executeUpdate();

            tblBrand.getItems().remove(selectedIndex);
            connection.commit();
        }catch (Throwable e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the brand name, try again!").show();
        }finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}