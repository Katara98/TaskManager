package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.volyk.tasks.Main;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.Controller;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.MainWindowController;

import java.io.IOException;

public class Window {
    private Stage stage;
    private Controller controller;

    public Stage getStage() {
        return stage;
    }

    public Controller getController() {
        return controller;
    }

    public Window(Stage stage, String file, String title) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(file));
        Parent root = (Parent) loader.load();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        controller = loader.getController();
    }

    public void showStage() {
        stage.show();
    }
}
