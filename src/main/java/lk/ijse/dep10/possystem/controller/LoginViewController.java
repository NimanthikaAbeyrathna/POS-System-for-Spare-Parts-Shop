package lk.ijse.dep10.possystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.dep10.possystem.db.DBConnection;
import lk.ijse.dep10.possystem.model.User;
import lk.ijse.dep10.possystem.util.PasswordEncoder;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginViewController {

    public Button btnLogin;
    public PasswordField txtPassword;
    public TextField txtUsername;
    
    public void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM User WHERE username=?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, username);
            ResultSet rst = stm.executeQuery();

            if (!rst.next()){
                txtUsername.getStyleClass().add("invalid");
                txtPassword.getStyleClass().add("invalid");
                txtUsername.requestFocus();
                txtUsername.selectAll();
            }else{
                if (!PasswordEncoder.matches(password, rst.getString("password"))){
                    txtUsername.getStyleClass().add("invalid");
                    txtPassword.getStyleClass().add("invalid");
                    txtUsername.requestFocus();
                    txtUsername.selectAll();
                    return;
                }

                String fullName = rst.getString("full_name");
                User.Role role = User.Role.valueOf(rst.getString("role"));
                User principal = new User(fullName, username, password, role);
                System.getProperties().put("principal", principal);
                URL mainViewUrl = getClass().getResource("/view/MainView.fxml");
                Scene mainViewScene = new Scene(FXMLLoader.load(mainViewUrl));
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setTitle("POS System");
                stage.setScene(mainViewScene);
                stage.sizeToScene();
                stage.centerOnScreen();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
