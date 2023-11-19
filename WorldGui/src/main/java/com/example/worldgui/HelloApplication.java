package com.example.worldgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    //    private TextArea messageTextArea;
//
//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();
//        Thread kafkaListenerThread = new Thread(HelloController.startKafkaListener());
//        kafkaListenerThread.setDaemon(true);
//        kafkaListenerThread.start();
//    }
//
//
//
//    public static void main(String[] args) {
//        launch();
//    }
//}


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
