module org.example.malabetuktuk {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.malabetuktuk to javafx.fxml;
    exports org.example.malabetuktuk;
}