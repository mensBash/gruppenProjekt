package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui;


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;


public class UserAlert {


    public static void showWarning(String s) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("User Warning");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    public static void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static boolean showConfirmation(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(s);
        alert.setContentText("");
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static void showError(String s){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(s);
        alert.showAndWait();
    }
}
