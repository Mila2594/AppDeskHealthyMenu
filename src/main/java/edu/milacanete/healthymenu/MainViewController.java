package edu.milacanete.healthymenu;

import edu.milacanete.healthymenu.model.FoodData;
import edu.milacanete.healthymenu.model.Food;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainViewController {

    @FXML
    public TableView <Food> tableFood;
    @FXML
    public Label lblFoodExists;
    @FXML
    public Button btnChart;
    @FXML
    public SplitPane splitPane;
    @FXML
    private TableColumn<Food, String> colFoodName;
    @FXML
    private TableColumn<Food, String> ColCategoryFood;
    @FXML
    private TableColumn<Food, Integer> ColWeightGram;
    @FXML
    private TableColumn<Food, Float> ColWeightOunce;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private ChoiceBox<String> choiceCategory;
    @FXML
    private TextField txtFoodName;
    @FXML
    private TextField txtWeight;
    @FXML
    private ObservableList<Food> food;

    public void initialize() {

        //el estado inicial del botón eliminar
        btnDelete.setDisable(true);

        //items categories de alimentos del elemento choice-box
        choiceCategory.setItems(
                FXCollections.observableArrayList(
                        "Frutas y verduras",
                        "Carnes y pescados",
                        "Derivados lácteos",
                        "Otros"
                )
        );

        // Cargar la lista inicial de alimentos desde el archivo
        List<Food> foodList = FoodData.getFoods();
        food = FXCollections.observableArrayList(foodList != null ? foodList : List.of());

        // Cargar los datos en la tabla
        colFoodName.setCellValueFactory(new PropertyValueFactory<>("name"));
        ColCategoryFood.setCellValueFactory(new PropertyValueFactory<>("category"));
        ColWeightGram.setCellValueFactory(new PropertyValueFactory<>("weightGram"));
        ColWeightOunce.setCellValueFactory(new PropertyValueFactory<>("weightInOz"));
        tableFood.setItems(food);


        // Listener para verificar si el alimento ya existe mientras se escribe
        txtFoodName.textProperty().addListener((_, _, _) -> validateFoodExists());

        //TextFormatter para validar que no se ingresen caracteres prohibidos
        setupFormatterNameFood();

        // Listener para deshabilitar el botón eliminar cuando no hay un alimento seleccionado
        tableFood.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> btnDelete.setDisable(newSelection == null));
    }

    // Asociado al botón Add
    // Método para agregar alimentos a la tabla, lista y archivo
    @FXML
    void addFood() {
        String name = txtFoodName.getText();
        String category = choiceCategory.getSelectionModel().getSelectedItem();
        String weightGram = txtWeight.getText();

        //validar campos de entrada si hay alguno vacío
        if (validateFieldsEmpty(name,category,weightGram)) return;

        //validar peso ingresado sea un entero
        Integer weightValidation = validateWeightInt(weightGram);
        if (weightValidation == null) {
            txtWeight.clear();
            return;
        }

        //validar si el alimento ya existe
        if (isFoodExists(name))return;

        //Crear un nuevo objeto Food, agregarlo a la lista de alimentos y actualizar la tabla
        Food newFood = new Food(name, category, weightValidation);
        food.add(newFood); //agregarlo a la lista
        tableFood.setItems(food); //actualizar la tabla

        //guardar la lista de alimentos en el archivo
        FoodData.saveFood(food);
        cleanFields();
    }

    //validar campos de entrada si hay alguno vacío
    private static boolean validateFieldsEmpty(String name, String category, String weight) {
        if (name.isEmpty() || category == null || weight.isEmpty()) {
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error");
            dialog.setHeaderText("Error al agregar datos");
            dialog.setContentText("Ningún campo puede estar vacío");
            dialog.showAndWait(); //se bloquea el flujo del programa hasta que se cierre la alerta
            return true;
        }
        return false;
    }

    // Validar si el alimento ya existe, si el nombre coincide
    private boolean isFoodExists(String name) {
        return food.stream().anyMatch(f ->  f.getName().equalsIgnoreCase(name)
        );
    }

    // Formatea el nombre del alimento ingresado para validar que no contenga el carácter prohibido ";"
    private void setupFormatterNameFood(){
        txtFoodName.setTextFormatter(
                new TextFormatter<>(change -> {
            if (change.getText().contains(";")){ // sí contiene el carácter prohibido muestra una alerta
                showAlertInvalidCharacter();
                return null; //devuelve null para cancelar el cambio, el carácter no se ingresa
            }
            return change;
        }));
    }

    // Alerta de error al ingresar el carácter prohibido
    private void showAlertInvalidCharacter() {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setTitle("Error");
        dialog.setHeaderText("Error al agregar datos");
        dialog.setContentText("El nombre del alimento no puede contener ;");
        dialog.showAndWait(); //se bloquea el flujo del programa hasta que se cierre la alerta
    }

    // Validar que el peso ingresado sea un entero
    private static Integer validateWeightInt(String weightGram) {
        try {
            return Integer.parseInt(weightGram);
        }catch (NumberFormatException e) {
            //mostrar alerta de error si el peso no es un entero
            Alert dialog = new Alert(Alert.AlertType.ERROR);
            dialog.setTitle("Error peso ingresado");
            dialog.setHeaderText("");
            dialog.setContentText("El peso debe ser un numero entero");
            dialog.showAndWait(); //se bloquea el flujo del programa hasta que se cierre la alerta
            return null;
        }
    }

    //Método para verificar si el alimento ya existe
    @FXML
    private void validateFoodExists() {

        String foodName = txtFoodName.getText();
        boolean existsFood = isFoodExists(foodName);

        //Si el alimento ya existe, deshabilitar el botón de agregar y mostrar etiqueta de alerta
        if (existsFood) {
            lblFoodExists.setText("El alimento ya existe");
            btnAdd.setDisable(true);
        } else {
            lblFoodExists.setText("");
            btnAdd.setDisable(false);
        }
    }

    // Método auxiliar para limpiar los campos de entrada y selección de la categoría
    private void cleanFields() {
        //limpiar los campos de entrada
        txtFoodName.clear();
        choiceCategory.getSelectionModel().clearSelection();
        txtWeight.clear();
    }

    //Método asociado al botón Delete
    // Valida la selección y confirmación del usuario para eliminar un alimento
    @FXML
    void deleteFood() {
        //obtener item seleccionado de la tabla
        Food selectedFood = tableFood.getSelectionModel().getSelectedItem();

        //validar que se haya seleccionado un alimento y confirmado por el usuario para eliminarlo
        if (selectedFood != null && confirmDeletion(selectedFood)) {
            removeFood(selectedFood, btnDelete);
        }
    }

    // Confirmar la eliminación de un alimento
    private boolean confirmDeletion(Food selectedFood) {

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Confirmación");
        dialog.setHeaderText("¿Desea eliminar el elemento?");
        dialog.setContentText("Item: " + selectedFood.getDescription());
        //Sí se presiona OK se elimina el alimento, Sí se presiona Cancelar se cancela la eliminación
        return dialog.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    // Método que elimina un alimento de la lista, de la tabla y del archivo
    private void removeFood(Food selectedFood, Button btnDelete) {

        food.remove(selectedFood); //eliminar de la lista
        tableFood.getItems().remove(selectedFood); //eliminar de la tabla
        tableFood.getSelectionModel().clearSelection();
        btnDelete.setDisable(true);
        //actualizar la lista de alimentos en el archivo
        FoodData.saveFood(food);
    }

    // Método para obtener la lista de alimentos y pasarla al gráfico de torta
    public ObservableList<Food> getFood() {
        return food;
    }

    // Asociado al botón Chart>
    // Muestra una nueva ventana con el gráfico
    @FXML
    void goToChartView(ActionEvent event) throws IOException {

        //Muestra una alerta si la lista de alimentos está vacía,
        // para que el usuario continue hacia la nueva ventana o no
        if (alertEmptyList(isListEmpty())){
            txtFoodName.requestFocus();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("chart-view.fxml"));
        Parent view1 = loader.load();
        Scene view1Scene = new Scene(view1);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
        stage.setScene(view1Scene);
        stage.show();
    }

    // Método auxiliar para verificar si la lista de alimentos esta vacía
    private boolean isListEmpty() {
        return food.isEmpty();
    }

    //alerta si la lista está vacía cuando se acciona el botón chart
    private boolean alertEmptyList(boolean isEmpty) {
        // Texto para complementar el content del alert.
        // Sí hay datos en los campos de entrada, se muestra el mensaje de perdida de dados ingresado (sin agregar)
        // Sí no hay datos ingresados, se muestra el mensaje para agregar un nuevo alimento
        String contentText = (!txtFoodName.getText().isEmpty() ||
                !txtWeight.getText().isEmpty() ||
                choiceCategory.getSelectionModel().getSelectedItem() != null) ?
                "Los datos ingresados se perderán." :
                "Para ingresar un nuevo alimento presione Cancelar.";
        String buttonAccept = (!txtFoodName.getText().isEmpty() ||
                !txtWeight.getText().isEmpty() ||
                choiceCategory.getSelectionModel().getSelectedItem() != null) ?
                "Ir de todos modos" : "Aceptar";

        if (isEmpty) {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Lista vacía");
            dialog.setHeaderText("");
            dialog.setContentText("No hay alimentos en la lista, no hay datos para mostrar." +contentText);
            //botones
            ButtonType continueBtn = new ButtonType(buttonAccept);
            ButtonType cancelBtn = new ButtonType("Cancelar");
            dialog.getButtonTypes().setAll(continueBtn,cancelBtn);
            // Sí se presiona buttonAccept va a la ventana de gráficos
            return dialog.showAndWait().filter(response -> response == cancelBtn).isPresent();
        }
        return false;
    }
}
