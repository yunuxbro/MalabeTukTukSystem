package org.example.malabetuktuk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Load the layout file from the root of your resources folder
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/malabetuktuk/main.fxml"));

        // 2. Set the window frame size bounds (matching your 1000x650 low-fi scale)
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);

        // 3. Configure and present the window
        stage.setTitle("Malabe Tuk Tuk - System Wireframe");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}