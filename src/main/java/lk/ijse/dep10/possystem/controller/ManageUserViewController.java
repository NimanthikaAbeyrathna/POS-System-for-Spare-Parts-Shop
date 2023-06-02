package lk.ijse.dep10.possystem.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.User;
import lk.ijse.dep10.possystem.util.PasswordEncoder;

import java.sql.*;

import java.util.regex.Pattern;

public class ManageUserViewController {

    public Button btnDeleteUser;
    public Button btnNewUser;
    public Button btnSaveUser;
    public TableView<User> tblUsers;
    public TextField txtFullName;
    public TextField txtUsername;
    public PasswordField txtPassword;

    public void initialize(){
        tblUsers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tblUsers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("username"));
        tblUsers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("role"));

        loadAllUsers();

        btnDeleteUser.setDisable(true);
        tblUsers.getSelectionModel().selectedItemProperty().addListener((ov, previous, current) -> {
            btnDeleteUser.setDisable(current == null);
        });

        tblUsers.getSelectionModel().selectedItemProperty().addListener((observableValue, previous, current) -> {
            if(current==null) return;

            String fullName = current.getFullName();
            String username = current.getUsername();
            String password = current.getPassword();

            txtFullName.setText(fullName);
            txtUsername.setText(username);
            txtPassword.setText(password);
        });
    }

    private void loadAllUsers() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rstUsers = stm.executeQuery("SELECT * FROM User");

            while (rstUsers.next()){
                String fullName = rstUsers.getString("full_name");
                String username = rstUsers.getString("username");
                String password = rstUsers.getString("password");
                String role = rstUsers.getString("role");

                User user = new User(fullName, username, password, User.Role.valueOf(role));
                tblUsers.getItems().add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load customers").show();
        }
    }

    public void btnDeleteUserOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            String sql = "DELETE FROM User WHERE username=?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, tblUsers.getSelectionModel().getSelectedItem().getUsername());
            stm.executeUpdate();

            tblUsers.getItems().remove(tblUsers.getSelectionModel().getSelectedItem());
            if (tblUsers.getItems().isEmpty()) btnNewUser.fire();
            connection.commit();
        }catch (Throwable e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the user try again!").show();
        }finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    
    public void btnNewUserOnAction(ActionEvent event) {
        txtFullName.clear();
        txtUsername.clear();
        txtPassword.clear();

        tblUsers.getSelectionModel().clearSelection();
        txtFullName.requestFocus();

        txtFullName.getStyleClass().remove("invalid");
        txtUsername.getStyleClass().remove("invalid");
        txtPassword.getStyleClass().remove("invalid");
    }

    
    public void btnSaveUserOnAction(ActionEvent event) {
        if (!isDataValid()) return;
        try {
            User newUser = new User(txtFullName.getText(), txtUsername.getText(), PasswordEncoder.encode(txtPassword.getText()), User.Role.USER);

            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);

            User selectedUser = tblUsers.getSelectionModel().getSelectedItem();

            if(selectedUser == null){
                String sql = "INSERT INTO User (full_name, username, password, role) VALUES (?, ?, ?, 'USER')";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setString(1, newUser.getFullName());
                stm.setString(2, newUser.getUsername());
                stm.setString(3, newUser.getPassword());
                stm.executeUpdate();

                tblUsers.getItems().add(newUser);
            }else{
                String sql = "UPDATE User SET password=? WHERE username=?";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setString(1, newUser.getPassword());
                stm.setString(2, newUser.getUsername());

                ObservableList<User> userList = tblUsers.getItems();
                int selectedStudentIndex = userList.indexOf(selectedUser);
                userList.set(selectedStudentIndex, newUser);
                tblUsers.refresh();
            }
            btnNewUser.fire();
            connection.commit();
        } catch (Throwable e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the user try again!").show();
        }finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isDataValid() {
        boolean dataValid = true;
        txtFullName.getStyleClass().remove("invalid");
        txtUsername.getStyleClass().remove("invalid");
        txtPassword.getStyleClass().remove("invalid");

        String fullName = txtFullName.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        Pattern regEx4UpperCaseLetters = Pattern.compile("[A-Z]");
        Pattern regEx4LowerCaseLetters = Pattern.compile("[a-z]");
        Pattern regEx4Digits = Pattern.compile("[0-9]");
        Pattern regEx4Symbols = Pattern.compile("[~!@#$%^&*()_+]");

        if (!(regEx4UpperCaseLetters.matcher(password).find() &&
                regEx4LowerCaseLetters.matcher(password).find() &&
                regEx4Digits.matcher(password).find() &&
                regEx4Symbols.matcher(password).find() &&
                password.length() >= 5)){
            txtPassword.requestFocus();
            txtPassword.selectAll();
            txtPassword.getStyleClass().add("invalid");
            dataValid = false;
        }

        if (!username.matches("[A-Za-z0-9]{3,}")){
            txtUsername.requestFocus();
            txtUsername.selectAll();
            txtUsername.getStyleClass().add("invalid");
            dataValid = false;
        }

        if (!fullName.matches("[A-Za-z ]+")){
            txtFullName.requestFocus();
            txtFullName.selectAll();
            txtFullName.getStyleClass().add("invalid");
            dataValid = false;
        }

        return dataValid;
    }


    public void tblUsersOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) btnDeleteUser.fire();
    }

}
