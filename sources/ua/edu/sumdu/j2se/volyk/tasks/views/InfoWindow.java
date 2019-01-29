package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.scene.control.Alert;

public class InfoWindow {
    public InfoWindow(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
