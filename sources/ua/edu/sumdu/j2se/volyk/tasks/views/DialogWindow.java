package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Realizes methods for displaying dialog windows
 */
public class DialogWindow {
    /**
     * Shows the dialog and waits for the user response (in other words, brings
     * up a blocking dialog, with the returned value the users input)
     * @param title title for dialog window
     * @param header text to show in the dialog header area
     * @param text text to show in the dialog content area
     * @param type alert type of the dialog window
     * @return value the users input
     */
    private static Optional<ButtonType> showDialogWindow(String title, String header, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        return alert.showAndWait();
    }

    /**
     * Shows the warning dialog
     * @param header text to show in the dialog header area
     * @param text text to show in the dialog content area
     */
    public static void showWarningWindow(String header, String text) {
        showDialogWindow("Warning!", header, text, Alert.AlertType.WARNING);
    }

    /**
     * Shows the information dialog
     * @param text text to show in the dialog content area
     */
    public static void showInfoWindow(String text) {
        showDialogWindow("Information Dialog", null, text, Alert.AlertType.INFORMATION);
    }

    /**
     * Shows the error dialog
     * @param text text to show in the dialog content area
     */
    public static void showErrorWindow(String text) {
        showDialogWindow("Error Dialog", "Error!", text, Alert.AlertType.ERROR);
    }

    /**
     * Shows the confirmation dialog and returns {@code true} if user pressed "OK" button, otherwise {@code false}
     * @param header text to show in the dialog header area
     * @param text text to show in the dialog content area
     * @return {@code true}  if user pressed "OK" button
     */
    public static boolean showConfirmationWindow(String header, String text) {
        Optional<ButtonType> result = showDialogWindow("Confirmation Dialog", header, text, Alert.AlertType.CONFIRMATION);
        return result.get() == ButtonType.OK;
    }
}
