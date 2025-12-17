module org.example.gui_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gui_client to javafx.fxml;
    exports org.example.gui_client;
}