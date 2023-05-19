package lk.ijse.dep10.possystem.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.NewBatch;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class NewBatchScenecontroller {

    public Button btnNew;
    public TextField txtSearch;
    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private DatePicker dtpDate;

    @FXML
    private TableView<NewBatch> tblBatchSummary;

    @FXML
    private TextField txtBatchNo;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtTotal;

    public void initialize() {
        loadBatch();
        tblBatchSummary.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        tblBatchSummary.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        tblBatchSummary.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("batchNo"));
        tblBatchSummary.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("date"));
        tblBatchSummary.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        tblBatchSummary.getSelectionModel().selectedItemProperty().addListener((observableValue, newBatch, current) -> {
            btnDelete.setDisable(current == null);

        });
        search();
    }

    private void generateId() {
        boolean validation = true;
        String supplierId = txtId.getText();
        String first = "1";
        String id = supplierId + first;
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> collectionOfBatchNumber = new ArrayList<>();

        for (NewBatch item : tblBatchSummary.getItems()) {
            int supplierId1 = item.getSupplierId();
            ids.add(supplierId1);
        }

        ArrayList<Integer> numberList = new ArrayList<>();
        if (id()) {
            for (Integer existIds : ids) {
                if (existIds != Integer.parseInt(supplierId)) {
                    validation = true;
                } else {
                    validation = false;
                }
            }
            if (tblBatchSummary.getItems().isEmpty() || validation) {
                txtBatchNo.setText(id);

            } else {

                ObservableList<NewBatch> items = tblBatchSummary.getItems();
                for (NewBatch item : items) {
                    if (supplierId.equals(Integer.toString(item.getSupplierId()))) {
                        int batchNo = item.getBatchNo();
                        collectionOfBatchNumber.add(batchNo);
                    }
                }
                Integer integer = collectionOfBatchNumber.get(collectionOfBatchNumber.size() - 1);
                int newBatchNumber = integer + 1;
                txtBatchNo.setText(Integer.toString(newBatchNumber));


            }
        }
//        else {
//            ObservableList<NewBatch> items = tblBatchSummary.getItems();
//
//            for (NewBatch item : items) {
//                int batchNo = item.getBatchNo();
//                String number = Integer.toString(batchNo);
//                char[] chars = number.toCharArray();
//                if (chars.length == 2) {
//                    if (Integer.toString(batchNo).substring(0, 1).equals(supplierId)) {
//                        numberList.add(batchNo);
//                    }
//                } else {
//                    if (Integer.toString(batchNo).substring(0, 2).equals(supplierId)) {
//
//                        numberList.add(batchNo);
//                    }
//                }
//            }
//            Integer number = numberList.get(numberList.size() - 1);
//            int newId = number + 1;
//            txtBatchNo.setText(Integer.toString(newId));
//
//        }
    }

    private boolean id() {
        boolean ids = true;
        int enteredId = parseInt(txtId.getText());
        ArrayList<Integer> collectionsOfIds = new ArrayList<>();
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT id FROM Supplier ";

//        ObservableList<NewBatch> items = tblBatchSummary.getItems();
//        for (NewBatch item : items) {
//            if (item.getSupplierId() != parseInt(txtId.getText())) {
//                ids = true;
//            } else {
//                ids = false;
//
//            }
//        }

        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            ResultSet rst = prd.executeQuery();

            while (rst.next()) {
                int id = rst.getInt(1);
                collectionsOfIds.add(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Integer collectionsOfId : collectionsOfIds) {
            if (enteredId == collectionsOfId) {
                ids = true;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please Enter Valid Supplier Id");
                alert.showAndWait();
                ids = false;
                txtId.requestFocus();
                txtId.clear();


            }
        }
        return ids;
    }

    private void loadBatch() {


        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT *FROM Batches";
        try {
            PreparedStatement prd = connection.prepareStatement(sql);

            ResultSet rst = prd.executeQuery();
            while (rst.next()) {
                System.out.println("rstnext");
                int supplierId = rst.getInt("supplier_id");
                String supplierName = rst.getString("supplier_name");
                int batchNo = rst.getInt("batch_no");
                Date date = rst.getDate("date");
                BigDecimal total = rst.getBigDecimal("total");
                NewBatch newBatch = new NewBatch(supplierId, supplierName, batchNo, date, total);
                tblBatchSummary.getItems().add(newBatch);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        tblBatchSummary.getSelectionModel().selectedItemProperty().addListener((observableValue, newBatch, current) -> {
            if (current != null) {
                Date date = current.getDate();
                int batchNo = current.getBatchNo();
                BigDecimal total = current.getTotal();
                int supplierId = current.getSupplierId();
                BigDecimal bigDecimal = new BigDecimal(String.valueOf(total));

                txtId.setText(Integer.toString(supplierId));
                txtBatchNo.setText(Integer.toString(batchNo));
                txtTotal.setText(bigDecimal.toString());
                dtpDate.setValue(date.toLocalDate());
            }
        });
    }


    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        NewBatch selectedItem = tblBatchSummary.getSelectionModel().getSelectedItem();
        tblBatchSummary.getItems().remove(selectedItem);

    }

    private boolean isDataValidate() {
        boolean dataValidate = true;

        if (!(txtId.getText().matches("\\b\\d{1,2}\\b"))) {
            new Alert(Alert.AlertType.INFORMATION, "Please add only a number");
            txtId.selectAll();
            txtId.requestFocus();
            dataValidate = false;
        }
        if (!(txtTotal.getText().matches("^\\d+(\\.\\d{1,2})?$"))) {
            System.out.println("total");
            new Alert(Alert.AlertType.INFORMATION, "Please add Correct Value");
            txtTotal.selectAll();
            txtTotal.requestFocus();
            dataValidate = false;
        }
        return dataValidate;
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        if (!(isDataValidate())) {
            return;
        }

        int id = parseInt(txtId.getText());
        int batchNumber = parseInt(txtBatchNo.getText());
        Date date = Date.valueOf(dtpDate.getValue());
        BigDecimal total = new BigDecimal(txtTotal.getText());

        NewBatch newBatch = new NewBatch(id, suplierName(), batchNumber, date, total);
        NewBatch selectedItem = tblBatchSummary.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        if (selectedItem == null) {
            tblBatchSummary.getItems().add(newBatch);
            String sql = "INSERT INTO Batches (supplier_id, supplier_name, batch_no, date, total) VALUES ('%d','%s','%d','%s','%f' )";
            sql = String.format(sql, id, suplierName(), batchNumber, date, total);

            try {
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {

            String sql = "UPDATE Batches SET supplier_id='%s',date='%s',total='%f' WHERE batch_no='%d'";
            sql = String.format(sql, id, date, total, batchNumber);
            try {
                PreparedStatement prd = connection.prepareStatement(sql);
                prd.executeUpdate();
                tblBatchSummary.getItems().remove(selectedItem);
                tblBatchSummary.getItems().add(newBatch);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        txtTotal.clear();
        dtpDate.setValue(null);
        txtId.clear();
        txtBatchNo.clear();
    }

    private String suplierName() {
        String name = "";
        Connection connection = DBConnection.getInstance().getConnection();
        String sql = "SELECT *FROM Supplier WHERE id=?";
        String id = txtId.getText();
        int ids = parseInt(id);
        try {
            PreparedStatement prd = connection.prepareStatement(sql);
            prd.setInt(1, ids);
            ResultSet rst = prd.executeQuery();
            if (rst.next()) {
                name = rst.getString("name");
                System.out.println("name");
            }
            return name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnNewOnAction(ActionEvent actionEvent) {
        generateId();
        txtTotal.clear();
        dtpDate.setValue(null);
        btnDelete.setDisable(true);

    }


    private void search() {

        txtSearch.textProperty().addListener((observableValue, s, current) -> {

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT *FROM Batches WHERE supplier_id LIKE '%1$s' OR supplier_name LIKE '%1$s' OR batch_no LIKE '%1$s' OR total LIKE '%1$s' OR date LIKE '%1$s'";
                sql = String.format(sql, '%' + current + '%');
                ResultSet rst = stm.executeQuery(sql);

                tblBatchSummary.getItems().clear();
                while (rst.next()) {
                    int supplierId = rst.getInt("supplier_id");
                    String supplierName = rst.getString("supplier_name");
                    int batchNo = rst.getInt("batch_no");
                    Date date1 = rst.getDate("date");
                    BigDecimal total = rst.getBigDecimal("total");
                    NewBatch newBatch = new NewBatch(supplierId, supplierName, batchNo, date1, total);
                    tblBatchSummary.getItems().add(newBatch);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


    }
}