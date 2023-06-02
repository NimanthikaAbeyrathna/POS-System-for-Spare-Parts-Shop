package lk.ijse.dep10.possystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.Bill;
import lk.ijse.dep10.possystem.model.BillDescription;
import lk.ijse.dep10.possystem.model.Supplier;

import java.sql.*;
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


    public void initialize() {

        tblSupplierDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblSupplierDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblSupplierDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("contact"));

        lstContact.getSelectionModel().selectedItemProperty().addListener((observableValue, value, current) -> {
            btnRemove.setDisable(current == null);
        });
        loadSuppliers();
        tblSupplierDetails.getSelectionModel().selectedItemProperty().addListener((observableValue, supplier, t1) -> {
            if (t1 != null) {
                btnDelete.setDisable(false);
            }
        });

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
        ObservableList<Supplier> supplierList = tblSupplierDetails.getItems();

        try {
            String sql = "SELECT * FROM Supplier";
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String contact = rst.getString("contact");
                ArrayList<String> contacts = new ArrayList<>();
                contacts.add(contact);

                ObservableList<String> observableList = FXCollections.observableList(contacts);
                Supplier supplier = new Supplier(id, name, contacts);
                supplierList.add(supplier);
            }


            tblSupplierDetails.getSelectionModel().selectedItemProperty().addListener((observableValue, value, current) -> {
                if (current != null) {
                    txtName.setText(current.getName());
                    txtId.setText(String.valueOf(current.getId()));
                    lstContact.setItems(FXCollections.observableArrayList(current.getContact()));
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isValidate() {
        boolean validate = true;

        if (!(txtName.getText().matches("[A-Za-z ]+"))) {
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

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            Statement stmt = connection.createStatement();
            int supplierId = tblSupplierDetails.getSelectionModel().getSelectedItem().getId();

            // Delete items associated with the supplier
            String deleteItemsSql = "DELETE FROM Items WHERE batch_num IN (SELECT batch_no FROM Batches WHERE supplier_id = %d)";
            deleteItemsSql = String.format(deleteItemsSql, supplierId);
            stmt.executeUpdate(deleteItemsSql);

            // Delete batches associated with the supplier
            String deleteBatchesSql = "DELETE FROM Batches WHERE supplier_id = %d";
            deleteBatchesSql = String.format(deleteBatchesSql, supplierId);
            stmt.executeUpdate(deleteBatchesSql);

            // Delete the supplier
            String deleteSupplierSql = "DELETE FROM Supplier WHERE id = %d";
            deleteSupplierSql = String.format(deleteSupplierSql, supplierId);
            stmt.executeUpdate(deleteSupplierSql);

            tblSupplierDetails.getItems().remove(tblSupplierDetails.getSelectionModel().getSelectedItem());
            connection.commit();
        } catch (Throwable e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the supplier, try again!").show();
        } finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


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

        int id = Integer.parseInt(txtId.getText());
        String name = txtName.getText();
        ObservableList<String> items = lstContact.getItems();
        ArrayList<String> list = new ArrayList<>(items);

        if (selectedItem == null) {

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                String sql = "INSERT INTO Supplier (id, name, contact) VALUES ('%d','%s','%s')";
                sql = String.format(sql, id, name, list);
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.executeUpdate();
                Supplier supplier = new Supplier(id, name, list);
                tblSupplierDetails.getItems().add(supplier);
                btnNewSupplier.fire();
                connection.commit();
            } catch (Throwable e) {
                try {
                    DBConnection.getInstance().getConnection().rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to save the supplier, try again!").show();
            } finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        } else {

            try {
                Connection connection = DBConnection.getInstance().getConnection();
                connection.setAutoCommit(false);

                String sqlUpdate = "UPDATE Supplier SET name='%s',contact='%s' WHERE id='%d'";
                sqlUpdate = String.format(sqlUpdate, name, list, id);
                PreparedStatement prdUpdate = connection.prepareStatement(sqlUpdate);
                prdUpdate.executeUpdate();

                Supplier supplier = new Supplier(id, name, list);
                int selectedSupplierIndex = tblSupplierDetails.getSelectionModel().getSelectedIndex();
                tblSupplierDetails.getItems().set(selectedSupplierIndex, supplier);
                connection.commit();
            } catch (Throwable e) {
                try {
                    DBConnection.getInstance().getConnection().rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to update the supplier, try again!").show();
            } finally {
                try {
                    DBConnection.getInstance().getConnection().setAutoCommit(true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
