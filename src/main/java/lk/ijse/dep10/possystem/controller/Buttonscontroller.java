package lk.ijse.dep10.possystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class Buttonscontroller {

    public Button btnBrand;
    public Button listOfBikes;
    public Button btnCategory;
    public Button btnParts;
    @FXML
    private Button btnNewBatch;

    @FXML
    private Button btnNewItems;

    @FXML
    private Button btnSupplier;

    Stage stage = new Stage();

    @FXML
    void btnNewBatchOnAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/NewBatchScene.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Batches");
        stage.setMaximized(true);
        stage.show();


    }

    @FXML
    void btnNewItemsOnActioin(ActionEvent event) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/ItemScene.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Items");
        stage.show();
        stage.setMaximized(true);
    }

    @FXML
    void btnSupplierOnAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/NewSupplierScene.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Suppliers ");
        stage.setMaximized(true);
        stage.show();

    }

    public void btnBrandOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/BrandNames.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Brand Names");
        stage.setMaximized(true);
        stage.show();

    }

    public void listOfBikesOnAction(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/ListOfBikes.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Bike Names");
        stage.setMaximized(true);
        stage.show();
    }

    public void btnCategoryOnAction(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/PartsCategory.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Categories");
        stage.setMaximized(true);
        stage.show();
    }

    public void btnPartsOnAction(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/PartsNames.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Add New Parts Names");
        stage.setMaximized(true);
        stage.show();

    }
}