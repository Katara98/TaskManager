package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmationWindow {
    private boolean ok;
    public ConfirmationWindow(String header, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            this.ok = true;
        } else {
            this.ok = false;
        }
    }

    public boolean isOk() {
        return ok;
    }
}
