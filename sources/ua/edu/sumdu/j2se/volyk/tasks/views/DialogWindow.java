package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogWindow {
    private static Optional<ButtonType> showDialogWindow(String title, String header, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        return alert.showAndWait();
    }
    public static void showWarningWindow(String header, String text) {
        showDialogWindow("Warning!", header, text, Alert.AlertType.WARNING);
    }

    public static void showInfoWindow(String text) {
        showDialogWindow("Information Dialog", null, text, Alert.AlertType.INFORMATION);
    }

    public static void showErrorWindow(String text) {
        showDialogWindow("Error Dialog", "Error!", text, Alert.AlertType.ERROR);
    }

    public static boolean showConfirmationWindow(String header, String text) {
        Optional<ButtonType> result = showDialogWindow("Confirmation Dialog", header, text, Alert.AlertType.CONFIRMATION);;
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}
