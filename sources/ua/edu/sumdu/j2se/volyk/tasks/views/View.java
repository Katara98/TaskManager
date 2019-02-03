package ua.edu.sumdu.j2se.volyk.tasks.views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class View {
    private Stage stage;

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

    public void showStage() {
        stage.show();
    }
}
