module gestionRH {
    requires javafx.fxml;

    requires javafx.controls;
    requires java.sql;

    opens test to javafx.fxml;
    opens controllers to javafx.fxml;
    exports test;
    exports controllers;
}