package ua.edu.sumdu.j2se.volyk.tasks;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.volyk.tasks.controllers.MainWindowController;
import ua.edu.sumdu.j2se.volyk.tasks.views.MainWindowView;
import ua.edu.sumdu.j2se.volyk.tasks.views.View;

public class Main extends Application {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting TaskManager application");
        log.debug("Creating main view window from: /MainWindow.fxml");
        MainWindowView view = (MainWindowView) View.loadViewFromFxml(primaryStage, "/MainWindow.fxml", "TaskManager");
        MainWindowController controller = new MainWindowController(view);
        view.setController(controller);
        view.showStage();
    }
}
