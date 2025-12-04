module GestionHotel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    opens ui to javafx.graphics, javafx.fxml;
    opens models to javafx.base;
}