package lk.ijse.dep10.possystem.model;

import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Supplier implements Serializable {
    private int id;
    private String name;
    private ArrayList<String> contact;


    public ObservableList<String> getObservableList() {
        return FXCollections.observableList(contact);
    }

}
