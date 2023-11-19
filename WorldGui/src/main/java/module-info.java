module com.example.worldgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.beans;
    requires spring.kafka;
    requires kafka.clients;
    requires lombok;


    opens com.example.worldgui to javafx.fxml;
    exports com.example.worldgui;
}