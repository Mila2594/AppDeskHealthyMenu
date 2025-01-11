package edu.milacanete.healthymenu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HealthyMenuApplication extends Application {

    // Crea un logger para la clase
    private static final Logger logger = Logger.getLogger(HealthyMenuApplication.class.getName());

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setTitle("Healthy Menu");
            stage.setMinWidth(580);
            stage.setMinHeight(400);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cargar la aplicación.", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
