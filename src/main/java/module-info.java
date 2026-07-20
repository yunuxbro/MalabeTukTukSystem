module org.example.malabetuktuk {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.malabetuktuk to javafx.fxml;
    opens org.example.malabetuktuk.blueprints to javafx.base;
    exports org.example.malabetuktuk;
}