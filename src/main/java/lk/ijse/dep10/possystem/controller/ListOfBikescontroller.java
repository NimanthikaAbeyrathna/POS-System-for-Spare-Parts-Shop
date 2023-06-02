package lk.ijse.dep10.possystem.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.BrandNames;
import lk.ijse.dep10.possystem.model.ListOfBikes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ListOfBikescontroller {

    public ComboBox cmbBrand;
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView<ListOfBikes> tblBikes;

    @FXML
    private TextField txtInput;

    public void initialize() {
        tblBikes.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("bikes"));
        addBrandsToComboBox();
        cmbBrand.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            if (previous != null) {
                tblBikes.getItems().clear();
            }
            if (current != null) {
                loadDataFromDataBase();
            }
        });

        tblBikes.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            if (current != null)
                loadDataWhenSelect();

        });
    }

    private void loadDataFromDataBase() {
        Object selectedItem = cmbBrand.getSelectionModel().getSelectedItem();

        String selectedBrand = selectedItem.toString();

        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT bike FROM List_Of_Bikes WHERE brand_name=?";

        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, selectedBrand);
            ResultSet rst = prd.executeQuery();
            while (rst.next()) {
                String bike = rst.getString(1);
                ListOfBikes listOfBikes = new ListOfBikes(bike);
                tblBikes.getItems().add(listOfBikes);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDataWhenSelect() {
        ListOfBikes selectedItem = tblBikes.getSelectionModel().getSelectedItem();
        String bikes = selectedItem.getBikes();
        txtInput.setText(bikes);
    }

    private void addBrandsToComboBox() {
        ArrayList<String> brands = new ArrayList<>();
        String sql = "SELECT brand_name FROM Brands";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String brandName = rst.getString(1);
                brands.add(brandName);
            }
            cmbBrand.getItems().addAll(brands);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean dataValidation() {
        boolean dataValidate = true;
        if (!(txtInput.getText().matches(".+"))) {
            txtInput.requestFocus();
            txtInput.clear();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter the Bike Name in Correct Format");
            alert.showAndWait();
            dataValidate = false;
        }
        return dataValidate;
    }

    private boolean inputDataValidation() {
        ArrayList<String> list = new ArrayList<>();

        boolean inputValid = true;

        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT * FROM List_Of_Bikes";

        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String bikeName = rst.getString(2);
                list.add(bikeName);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (String bikes : list) {
            if (txtInput.getText().equals(bikes)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Name of bike is Already exist please enter a new name");
                alert.showAndWait();
                txtInput.clear();
                txtInput.requestFocus();
                inputValid = false;
            }
        }

        return inputValid;
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!(dataValidation() && inputDataValidation()) && (cmbBrand.getSelectionModel().getSelectedItem() != null)) {
            return;
        }

        Object selectedItem = cmbBrand.getSelectionModel().getSelectedItem();
        String selectedBrand = selectedItem.toString();
        String modelOfBike = txtInput.getText();

        ListOfBikes selectedItem1 = tblBikes.getSelectionModel().getSelectedItem();
        if (selectedItem1 == null) {

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);
                String sql = "INSERT INTO List_Of_Bikes (brand_name, bike) VALUES (?,?)";
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setString(1, selectedBrand);
                prd.setString(2, modelOfBike);
                ListOfBikes listOfBikes = new ListOfBikes(modelOfBike);
                tblBikes.getItems().add(listOfBikes);
                txtInput.clear();
                prd.executeUpdate();
                connection.commit();
            }catch (Throwable e) {
                try {
                    DBConnection.getInstance().getConnection().rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save the bike try again!").show();
            }finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            ListOfBikes selectedItem2 = tblBikes.getSelectionModel().getSelectedItem();
            String bikes = selectedItem2.getBikes();


            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                String sql = "UPDATE List_Of_Bikes SET bike=? WHERE bike=?";
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setString(1, modelOfBike);
                prd.setString(2, bikes);
                tblBikes.getItems().remove(selectedItem2);
                ListOfBikes listOfBikes = new ListOfBikes(modelOfBike);
                tblBikes.getItems().add(listOfBikes);
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
                new Alert(Alert.AlertType.ERROR, "Failed to update the bike name try again!").show();
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
        int selectedIndex = tblBikes.getSelectionModel().getSelectedIndex();
        ListOfBikes selectedItem = tblBikes.getSelectionModel().getSelectedItem();
        String bikes = selectedItem.getBikes();
        tblBikes.getItems().remove(selectedIndex);

        String sql = "DELETE FROM List_Of_Bikes WHERE bike=?";
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setString(1, bikes);
            prd.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

