package lk.ijse.dep10.possystem.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lk.ijse.dep10.possystem.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainViewController {

    public Button btnManageCustomers;
    public Button btnManageItems;
    public Button btnMangeUsers;
    public Label lblDateTime;
    public Label lblUser;
    public Button btnBilling;
    public Button btnLoyalty;

    public void initialize(){
        User principal = (User) System.getProperties().get("principal");
        lblUser.setText(String.format("%s: %s",
                principal.getRole().name(), principal.getFullName()));
        btnMangeUsers.setVisible(principal.getRole() == User.Role.ADMIN);

        lblDateTime.setText(LocalDateTime.now().
                format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        KeyFrame key = new KeyFrame(Duration.seconds(1), event -> {
            lblDateTime.setText(LocalDateTime.now().
                    format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        });
        Timeline timeline = new Timeline(key);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();

        Platform.runLater(()->{
            var accelerators = btnMangeUsers.getScene().getAccelerators();
            accelerators.put(new KeyCodeCombination(KeyCode.F1), btnManageCustomers::fire);
            accelerators.put(new KeyCodeCombination(KeyCode.F2), btnManageItems::fire);
            accelerators.put(new KeyCodeCombination(KeyCode.F3), btnMangeUsers::fire);
        });
    }

    public void btnManageCustomersOnAction(ActionEvent event) throws IOException {
        openNewWindow("Manage Customers", btnManageCustomers.getScene().getWindow(), "CustomerView");
    }

    
    public void btnManageItemsOnAction(ActionEvent event) {

    }

    
    public void btnMangeUsersOnAction(ActionEvent event) throws IOException {
        openNewWindow("Manage Users", btnMangeUsers.getScene().getWindow(), "ManageUserView");
    }

    private void openNewWindow(String title, Window window, String file) throws IOException {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/" + file + ".fxml"))));
        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
        stage.centerOnScreen();
    }

    public void btnBillingOnAction(ActionEvent actionEvent) {
    }

    public void btnLoyalityOnAction(ActionEvent actionEvent) {
    }
}
