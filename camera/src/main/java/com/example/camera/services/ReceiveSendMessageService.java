package com.example.camera.services;

import com.example.camera.dto.KMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReceiveSendMessageService {
    private final KafkaTemplate kafkaTemplate;
    @Value("${kafka.topic}")
    private String topic;

    @KafkaListener(topics = "TargetBearingPosition")
    public void receiveBearingData(KMessage message) {
        int split = message.getMessage().indexOf(",");
        String bearingAngle = message.getMessage().substring(0, split);
        String distance = message.getMessage().substring(split);

        double cameraAngle = newCameraAngle(bearingAngle, distance);
        double CameralostStatus = 0;
        if (cameraAngle > 0)
            CameralostStatus = 1;

        // Calculate camera angle based on the received target's angle

        // Update camera viewpoint and report to Kafka
        KMessage kMessage = new KMessage();
        kMessage.setMessage(String.valueOf(CameralostStatus));
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), kMessage);
    }


    private double newCameraAngle(String bearingAngle, String distance) {
        // Assuming the camera's default angle is 0 degrees when facing the positive X-axis
        // Example: Transform the target's angle to adjust for the camera's angle or viewpoint
        double cameraDefaultAngle = 20; // Angle when the camera faces the positive X-axis
        double adjustedCameraAngle = (Double.parseDouble(distance) - cameraDefaultAngle);

        // Normalize the angle to be within 0 to 360 degrees
        adjustedCameraAngle = ((adjustedCameraAngle % 360) - Double.parseDouble(bearingAngle)) % 360;

        return adjustedCameraAngle;
    }


}
