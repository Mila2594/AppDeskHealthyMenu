package edu.milacanete.healthymenu;

import edu.milacanete.healthymenu.model.Food;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChartViewController {

    @FXML
    public Button btnGoToMain;
    @FXML
    private PieChart pieChartFoods;

    // Logger para la clase
    private static final Logger logger = Logger.getLogger(ChartViewController.class.getName());

    public void initialize() {

        try {
            // Carga el archivo FXML y accede al controlador asociado
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            loader.load(); // Válida y carga el archivo FXML
            MainViewController controller = loader.getController();

            // Obtiene la lista de alimentos del controlador principal
            List<Food> food = controller.getFood();

            // Configura el gráfico de pastel
            pieChartFoods.getData().clear();
            pieChartFoods.setTitle("Gráfico de alimentos");

            // Agrupa alimentos por categoría y suma los pesos en gramos
            Map<String, Integer> result = food.stream()
                    .collect(Collectors.groupingBy(
                            Food::getCategory,
                            Collectors.summingInt(Food::getWeightGram)
                    ));

            // Agrega los datos al gráfico
            result.forEach((category, weight) -> pieChartFoods.getData().add(new PieChart.Data(category, weight)));
        }catch (Exception e){
            pieChartFoods.setTitle("Error al cargar el gráfico.");
            logger.log(Level.SEVERE, "Error al cargar la vista del gráfico.", e);
        }
    }

    // Asociado al botón Back
    // Vuelve a la vista principal del formulario con la tabla
    @FXML
    void goToBackMain(ActionEvent event) throws IOException {
        // Carga la vista principal y cambia la escena
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent view1 = loader.load();
        Scene view1Scene = new Scene(view1);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.hide();
        stage.setScene(view1Scene);
        stage.show();
    }


}
