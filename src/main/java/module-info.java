module edu.milacanete.healthymenu {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    opens edu.milacanete.healthymenu to javafx.fxml;
    opens edu.milacanete.healthymenu.model to javafx.base;
    exports edu.milacanete.healthymenu;
    exports edu.milacanete.healthymenu.model;
}