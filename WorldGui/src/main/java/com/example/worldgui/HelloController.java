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
    private static final String TARGET_BEARING_TOPIC = "TargetPointPosition";
    private static final String TOPIC_CAMERA_STATUS = "CameraLosStatus";
    private static final String TOPIC_TOWER_POSITION = "TowerPosition";
    public Polygon radar;
    public Polygon camera;
    public Polygon target;
    public final static String kafkaAddress="127.0.0.1:9092";
    public final static String groupID="worldgui";

    @FXML
    private Button playButton;

    @FXML
    protected void onPlayButtonClick() {

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Thread kafkaListenerThread = new Thread(this::startKafkaListener);
                kafkaListenerThread.setDaemon(true);
                kafkaListenerThread.start();            }

            private void startKafkaListener() {
                Properties props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaAddress);
                props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

                Consumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList("TARGET_BEARING_TOPIC"));

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    records.forEach(record -> {
                        Platform.runLater(() -> listenTopicTargetPosition(record.value()));
                    });
                }
            }
        });

    }

    private void listenTopicTargetPosition(String message) {
        new Thread(() -> {
            // Process messages from TargetPointPosition topic
            int split = message.indexOf(",");
            int lastSplit = message.lastIndexOf(",");
            double x = Double.parseDouble(message.substring(0, split));
            double y = Double.parseDouble(message.substring(split, lastSplit));
            updateTargetPosition(x, y);

        }).start();
    }

    private void updateTargetPosition(double x, double y) {
        // Update the position of the target triangle
        target.setLayoutX(x);
        target.setLayoutY(y);
        target.setVisible(true); // Make it visible at the updated position
    }

}