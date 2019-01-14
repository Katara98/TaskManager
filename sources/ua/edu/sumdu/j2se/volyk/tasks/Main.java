package ua.edu.sumdu.j2se.volyk.tasks;

import javafx.application.Application;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.MainWindowController;
import ua.edu.sumdu.j2se.volyk.tasks.views.Window;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Window window = new Window(primaryStage, "MainWindow.fxml", "TaskManager");
        MainWindowController controller = (MainWindowController) window.getController();
        controller.setWindow(window);
        window.showStage();
    }
}
