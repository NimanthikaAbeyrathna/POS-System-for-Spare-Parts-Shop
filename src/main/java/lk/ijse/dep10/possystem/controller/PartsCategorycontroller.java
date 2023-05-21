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
import lk.ijse.dep10.possystem.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PartsCategorycontroller {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private TableView<Category> tblCategory;

    @FXML
    private TextField txtInput;

    public void initialize() {
        tblCategory.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("category"));
        tblCategory.getItems().addAll(loadDataFromDataBase());
        loadDataToFields();

    }

    private void loadDataToFields() {

        tblCategory.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            if (current != null) {
                Category selectedItem = tblCategory.getSelectionModel().getSelectedItem();
                String category = selectedItem.getCategory();
                txtInput.setText(category);
            }
        });
    }

    private boolean dataRepeatedValidation() {

        boolean validation = true;
        ObservableList<Category> items = tblCategory.getItems();
        for (Category item : items) {
            String categoryName = item.getCategory();
            if (txtInput.getText().equals(categoryName)) {
                System.out.println("if");
                Alert alert = new Alert(Alert.AlertType.ERROR, "This item Already in the System Please enter new one");
                alert.showAndWait();
                txtInput.clear();
                txtInput.requestFocus();
                validation = false;
            }
        }
        return validation;
    }

    private ArrayList<Category> loadDataFromDataBase() {
        ArrayList<Category> list = new ArrayList<>();
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT *FROM Parts_Category";
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                String category = rst.getString(1);
                Category category1 = new Category(category);
                list.add(category1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private boolean categoryValidation() {
        boolean category = true;

        if (!(txtInput.getText().matches("[A-Za-z ]+"))) {
            System.out.println("notMatch");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Category name is empty");
            alert.showAndWait();
            txtInput.requestFocus();
            txtInput.clear();
            category = false;
        }

        return category;
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!(categoryValidation() && dataRepeatedValidation())) {
            System.out.println("add");
            return;
        }
        System.out.println("after add");
        String partsCategory = txtInput.getText();
        Category category = new Category(partsCategory);

        if (tblCategory.getSelectionModel().getSelectedItem() == null) {
            String sql = "INSERT INTO Parts_Category (parts_category) VALUES (?)";
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.setString(1, partsCategory);
                tblCategory.getItems().add(category);
                txtInput.clear();
                prd.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sqlUpdate = "UPDATE Parts_Category SET parts_category=? WHERE parts_category=?";
            Connection connection1 = DBConnection.getInstance().getConnection();
            try {
                PreparedStatement prd = connection1.prepareStatement(sqlUpdate);
                Category selectedItem = tblCategory.getSelectionModel().getSelectedItem();
                prd.setString(1, partsCategory);
                prd.setString(2, selectedItem.getCategory());
                tblCategory.getItems().remove(selectedItem);
                tblCategory.getItems().add(category);
                prd.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

        Category selectedItem = tblCategory.getSelectionModel().getSelectedItem();
        String category = selectedItem.getCategory();


        String deleteSql = "DELETE FROM Parts_Category WHERE parts_category=?";

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement prd = connection.prepareStatement(deleteSql);
            prd.setString(1, category);
            tblCategory.getItems().remove(selectedItem);
            prd.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
