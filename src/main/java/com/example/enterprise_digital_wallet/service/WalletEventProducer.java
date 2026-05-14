package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.event.WalletEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletEventProducer {

    private final KafkaTemplate<String, WalletEvent> kafkaTemplate;

    @Value("${app.kafka.topics.wallet-events}")
    private String walletEventsTopic;

    public void publishWalletEvent(WalletEvent event) {
        kafkaTemplate.send(walletEventsTopic, event.referenceNumber(), event);
    }
}