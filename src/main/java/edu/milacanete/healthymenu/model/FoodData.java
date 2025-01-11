package edu.milacanete.healthymenu.model;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodData {

    // Crea un logger para la clase
    private static final Logger logger = Logger.getLogger(FoodData.class.getName());

    // Método de la clase para obtener la ruta absoluta de Food.txt en resources
    private static Path getFilePath() {
        return Paths.get(System.getProperty("user.dir"),"/src/Food.txt");
    }

    //método privado estático para leer archivo de datos txt y convertirlo en una lista
    private static List<Food> readFile() {

        Path path = getFilePath();
        //validar si el archivo existe
        if (!Files.exists(path)) {
            System.out.println("Error: no se encontró ruta del archivo \"Food.txt\"");
            return List.of();
        }

        // leer el archivo linea a linea, cada linea se convierta a un objeto Food
        try (Stream<String> lines = Files.lines(path)) {
            List<String> errorLines = new ArrayList<>(); // para almacenar las líneas con errores
            List<Food> foods = lines
                    .filter(line -> line.split(";").length >= 3) // Validar formato inicial, la linea tiene 3 partes
                    .map(line -> {
                        // captura la excepción si el peso no es un entero
                        try {
                            String[] parts = line.split(";");
                            return new Food(parts[0], parts[1], Integer.parseInt(parts[2]));
                        } catch (NumberFormatException e) {
                            // Registrar el error y añadir la línea a la lista de errores
                            logger.log(Level.WARNING, "Error al parsear una línea: " + line, e);
                            errorLines.add(line);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull) // Filtrar objetos nulos
                    .collect(Collectors.toList());

            //si se recolectan lineas con errores, mostrar en el log
            if (!errorLines.isEmpty()) {
                logger.log(Level.WARNING, "Se encontraron líneas con errores: " + errorLines);
            }

            return foods;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al leer el archivo \"Food.txt\"", e);
            return null;
        }
    }

    // Método publico para obtener la lista de objetos Food
    public static List<Food> getFoods() {
        return readFile();
    }

    //método privado estático para guardar datos en el archivo txt
    private static void saveFile(List<Food> food) {
        Path path = getFilePath();
        try {
            // Crea el directorio padre si no existe
            Files.createDirectories(path.getParent());

            // Escribe los datos en el archivo, sobrescribiendo cualquier contenido previo
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                food.forEach(f ->pw.println(f.toString()));
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar datos en \"Food.txt\"", e);
        }
    }

    // Método publico para guardar la lista de alimentos
    public static void saveFood(List<Food> food) {
        saveFile(food);
    }
}
