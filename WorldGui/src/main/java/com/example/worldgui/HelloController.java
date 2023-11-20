package com.example.worldgui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.Polygon;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class HelloController {
    private final String TARGET_BEARING_TOPIC = "TargetBearing";
    private final String CAMERA_LOS_STATUS_TOPIC = "CameraLostStatus";
    public Polygon radar;
    public Polygon camera;
    public Polygon target;

    public final static String kafkaUrl = "127.0.0.1:9092";
    public final static String radarGroupID = "radar";
    public final static String cameraGroupID = "camera";
    @FXML
    private Button playButton;

    @FXML
    protected void onPlayButtonClick() {

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Thread kafkaTargetListenerThread = new Thread(this::startKafkaTargetListener);
                kafkaTargetListenerThread.setDaemon(true);
                kafkaTargetListenerThread.start();

                Thread kafkaCameraStatusListenerThread = new Thread(this::startKafkaCameraStatusListener);
                kafkaCameraStatusListenerThread.setDaemon(true);
                kafkaCameraStatusListenerThread.start();
            }

            private void startKafkaCameraStatusListener() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
                props.put(ConsumerConfig.GROUP_ID_CONFIG, cameraGroupID);
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

                Consumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList(CAMERA_LOS_STATUS_TOPIC));

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    records.forEach(cameraStatusMessage -> Platform.runLater(() -> handleCameraStatus(cameraStatusMessage.value())));
                }
            }

            private void startKafkaTargetListener() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
                props.put(ConsumerConfig.GROUP_ID_CONFIG, radarGroupID);
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

                Consumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList(TARGET_BEARING_TOPIC));

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    records.forEach(targetMessage -> Platform.runLater(() -> handleTargetPosition(targetMessage.value())));
                }
            }
        });

    }

    private void handleCameraStatus(String message) {
        new Thread(() -> camera.setVisible(Boolean.parseBoolean(message))).start();
    }

    private void handleTargetPosition(String message) {
        new Thread(() -> {
            int split = message.indexOf(",");
            int lastSplit = message.lastIndexOf(",");
            double x = Double.parseDouble(message.substring(0, split));
            double y = Double.parseDouble(message.substring(split, lastSplit));
            updateTargetPosition(x, y);

        }).start();
    }

    private void updateTargetPosition(double x, double y) {
        target.setLayoutX(x);
        target.setLayoutY(y);
        target.setVisible(true);
    }

}