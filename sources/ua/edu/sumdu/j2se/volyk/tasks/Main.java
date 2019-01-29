package ua.edu.sumdu.j2se.volyk.tasks;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.MainWindowController;
import ua.edu.sumdu.j2se.volyk.tasks.views.Window;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting TaskManager application");
        log.debug("Creating main view window from: /MainWindow.fxml");
        Window window = new Window(primaryStage, "/MainWindow.fxml", "TaskManager");
        MainWindowController controller = (MainWindowController) window.getController();
        controller.setWindow(window);
        window.showStage();
    }
}
