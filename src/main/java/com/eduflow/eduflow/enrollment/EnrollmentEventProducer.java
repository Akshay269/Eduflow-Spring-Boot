package com.eduflow.eduflow.enrollment;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentEventProducer {

    private static final String TOPIC = "enrollment-events";
    private final KafkaTemplate<String, Object> kafkaTemplate;  // ← Object

    public void sendEnrollmentEvent(EnrollmentEvent event) {
        log.info("Sending enrollment event for student: {}", event.getStudentEmail());
        kafkaTemplate.send(TOPIC, event.getStudentId().toString(), event);
    }
}