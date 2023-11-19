package com.demo.radar.services;

import com.demo.radar.dto.KMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RadarSendMessageService {
    @Value("${kafka.topic}")
    private String topic;
    private final KafkaTemplate<String, KMessage> kafkaTemplate;
    double radarX = 0, radarY = 0, targetX = 0, targetY = 0;

    @KafkaListener(topics = {"TargetPointPosition", "TowerPosition"})
    public void receivePositionData(String message) {
        sendTargetPosition(message);
    }

    // Placeholder methods for angle and distance calculation (replace with actual logic)
    private double calculateDistance(String data) {
        // Your distance calculation logic based on received position data
        int towerPosition = 30;
        int targetPosition = 110;
        return targetPosition-towerPosition;
    }

    private double calculatedeltaX(String data) {
        double deltaX = targetX - radarX;
        return deltaX;
    }  private double calculatedeltaY(String data) {
        double deltaY = targetY - radarY;
        return deltaY;
    }

    public void sendTargetPosition(String message) {
        double deltaX = calculatedeltaX(message);
        double deltaY = calculatedeltaY(message);
        double distance = calculateDistance(message); // Calculate distance based on received position data
        StringBuilder calculateBearingAngle = new StringBuilder();
        // Calculate angle based on received position data
        calculateBearingAngle.append(deltaX);
        calculateBearingAngle.append(",");
        calculateBearingAngle.append(deltaY);
        calculateBearingAngle.append(",");
        calculateBearingAngle.append(distance);
        KMessage kMessage = new KMessage();
        kMessage.setMessage(String.valueOf(calculateBearingAngle));
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), kMessage);

    }

}
