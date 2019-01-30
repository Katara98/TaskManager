package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ErrorWindow {
    public ErrorWindow(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Error!");
        alert.setContentText(text);
        alert.showAndWait();
    }
}
