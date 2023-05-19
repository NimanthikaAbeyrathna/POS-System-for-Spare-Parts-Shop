package lk.ijse.dep10.possystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NewSupplierScenecontroller {

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnNewSupplier;

    @FXML
    private Button btnRemove;

    @FXML
    private Button btnSave;

    @FXML
    private ListView<String> lstContact;

    @FXML
    private TableView<Supplier> tblSupplierDetails;

    @FXML
    private TextField txtContact;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    ArrayList<String> list1 = new ArrayList<>();

    public void initialize() {

        tblSupplierDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblSupplierDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblSupplierDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("contact"));

        lstContact.getSelectionModel().selectedItemProperty().addListener((observableValue, value, current) -> {
            btnRemove.setDisable(current == null);
        });
        loadSuppliers();

//        tblSupplierDetails.getSelectionModel().selectedItemProperty().addListener((observableValue, value, current) ->{
//            if(current!=null){
//                txtName.setText(current.getName());
//                txtId.setText(Integer.toString(current.getId()));
//                lstContact.setItems(current.getObservableList());
//            }
//        } );


    }


    private void generateId() {
        int id = 1;
        ObservableList<Supplier> items = tblSupplierDetails.getItems();
        if (items.isEmpty()) {
            txtId.setText(Integer.toString(id));
        } else {
            int newId = (items.get((items.size() - 1)).getId()) + 1;
            txtId.setText(Integer.toString(newId));
        }
    }

    private void loadSuppliers() {
        Connection connection = DBConnection.getInstance().getConnection();

        String sql = "SELECT *FROM Supplier";
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();
            if (rst.next()) {
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String contact = rst.getString("contact");
                list1.add(contact);
                ObservableList<String> observableList = FXCollections.observableList(list1);
                Supplier supplier = new Supplier(id, name, list1);
                tblSupplierDetails.getItems().add(supplier);
                Supplier selectedItem = tblSupplierDetails.getSelectionModel().getSelectedItem();


                tblSupplierDetails.getSelectionModel().selectedItemProperty().addListener((observableValue, value, current) -> {
                    if (current != null) {
                        txtName.setText(name);
                        txtId.setText(Integer.toString(id));
                        lstContact.setItems(observableList);
                    }
                });

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidate() {
        boolean validate = true;

        if (!(txtName.getText().matches("[A-Za-z]+"))) {
            txtName.requestFocus();
            txtName.selectAll();
            validate = false;
        }
        if (lstContact.getItems().isEmpty()) {
            txtContact.requestFocus();
            validate = false;
        }

        return validate;

    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!(txtContact.getText().matches("\\d{3}-\\d{7}"))) {
            txtContact.selectAll();
            txtContact.requestFocus();
        } else {
            lstContact.getItems().add(txtContact.getText());
            txtContact.clear();
        }
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnNewSupplierOnAction(ActionEvent event) {
        generateId();
        txtContact.clear();
        txtName.clear();
        btnRemove.setDisable(true);
        btnDelete.setDisable(true);
        lstContact.getItems().clear();
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        lstContact.getItems().remove(lstContact.getSelectionModel().getSelectedIndex());
        txtContact.requestFocus();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!(isValidate())) {
            return;
        }
        Supplier selectedItem = tblSupplierDetails.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();
        ObservableList<String> items = lstContact.getItems();
        ArrayList<String> list = new ArrayList<>(items);
        if (selectedItem == null) {
            Supplier supplier = new Supplier(id, name, list);
            tblSupplierDetails.getItems().add(supplier);
            btnNewSupplier.fire();

            String sql = "INSERT INTO Supplier (id, name, contact) VALUES ('%d','%s','%s')";
            sql = String.format(sql, id, name, list);
            try {
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            tblSupplierDetails.getItems().remove(selectedItem);
            Supplier supplier1 = new Supplier(id, name, list);
            tblSupplierDetails.getItems().add(supplier1);


            String sqlUpdate = "UPDATE Supplier SET name='%s',contact='%s' WHERE id='%d'";
            sqlUpdate = String.format(sqlUpdate, name, list, id);
            try {
                PreparedStatement prdUpdate = connection.prepareStatement(sqlUpdate);
                prdUpdate.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
