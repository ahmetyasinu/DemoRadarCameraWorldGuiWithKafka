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

    @KafkaListener(topics = "TargetBearingPosition", groupId = "radar")
    public void receiveBearingData(KMessage message) {
        int split = message.getMessage().indexOf(",");
        String bearingAngle = message.getMessage().substring(0, split);
        String distance = message.getMessage().substring(split);

        double cameraAngle = newCameraAngle(bearingAngle, distance);
        boolean cameralostStatus = false;
        if (cameraAngle > 0)
            cameralostStatus = true;

        KMessage kMessage = new KMessage();
        kMessage.setMessage(String.valueOf(cameralostStatus));
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), kMessage);
    }


    private double newCameraAngle(String bearingAngle, String distance) {
        double cameraDefaultAngle = 20;
        double adjustedCameraAngle = (Double.parseDouble(distance) - cameraDefaultAngle);
        adjustedCameraAngle = ((adjustedCameraAngle % 360) - Double.parseDouble(bearingAngle)) % 360;
        return adjustedCameraAngle;
    }


}
