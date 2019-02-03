package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ua.edu.sumdu.j2se.volyk.tasks.views.*;

import java.util.Optional;

public abstract class Controller {
    public void showWarningWindow(String header, String text) {
        new WarningWindow(header, text);
    }

    public void showInfoWindow(String text) {
        new InfoWindow(text);
    }

    public void showErrorWindow(String text) {
        new ErrorWindow(text);
    }

    public boolean showConfirmationWindow(String header, String text) {
        ConfirmationWindow confirmationWindow = new ConfirmationWindow(header, text);
        if (confirmationWindow.isOk()) {
            return true;
        }
        return false;
    }
}
