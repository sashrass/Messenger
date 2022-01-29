module API {
    requires javafx.graphics;
    exports service;
    exports Roles;
    exports Data;

    opens service to javafx.graphics, javafx.fxml, javafx.base, javafx.controls;
    opens Data to javafx.graphics, javafx.fxml, javafx.base, javafx.controls;
    opens Roles to javafx.graphics, javafx.fxml, javafx.base, javafx.controls;
}