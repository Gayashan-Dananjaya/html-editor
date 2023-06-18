package lk.ijse.dep10.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.dep10.editor.controller.EditorSceneController;

import java.io.IOException;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/EditorScene.fxml"));
        primaryStage.setScene(new Scene(fxmlLoader.load()));

        EditorSceneController controller = fxmlLoader.getController();
        controller.initData(primaryStage);

        primaryStage.setTitle("Untitled Document");
        primaryStage.setHeight(800);
        primaryStage.setWidth(1300);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
