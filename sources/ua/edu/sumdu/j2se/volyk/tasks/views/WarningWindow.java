package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.scene.control.Alert;

public class WarningWindow {
    public WarningWindow(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
