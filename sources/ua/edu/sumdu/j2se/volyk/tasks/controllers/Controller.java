package ua.edu.sumdu.j2se.volyk.tasks.controllers;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ua.edu.sumdu.j2se.volyk.tasks.views.ConfirmationWindow;
import ua.edu.sumdu.j2se.volyk.tasks.views.InfoWindow;
import ua.edu.sumdu.j2se.volyk.tasks.views.WarningWindow;
import ua.edu.sumdu.j2se.volyk.tasks.views.Window;

import java.util.Optional;

public class Controller {
    protected Window window;

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public void showWarningWindow(String header, String text) {
        new WarningWindow(header, text);
    }

    public void showInfoWindow(String text) {
        new InfoWindow(text);
    }

    public boolean showConfirmationWindow(String header, String text) {
        ConfirmationWindow confirmationWindow = new ConfirmationWindow(header, text);
        if (confirmationWindow.isOk()) {
            return true;
        }
        return false;
    }
}
