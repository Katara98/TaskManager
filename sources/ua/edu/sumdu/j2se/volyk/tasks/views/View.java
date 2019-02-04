package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Represents a view of application
 */
public abstract class View {
    private Stage stage;

    /**
     * Loads view from fxml file
     * @param stage stage where view will be shown
     * @param file path to the fxml file
     * @param title title of the window
     * @return view created from fxml file
     * @throws IOException if an I/O error occurs.
     */
    public static View loadViewFromFxml(Stage stage, String file, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(View.class.getResource(file));
        Parent root = (Parent) loader.load();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        View view = loader.getController();
        view.stage = stage;
        return view;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * Shows this window
     */
    public void showStage() {
        stage.show();
    }
}
