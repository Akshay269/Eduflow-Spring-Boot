package com.eduflow.eduflow.notification;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.eduflow.eduflow.enrollment.EnrollmentEvent;
import com.eduflow.eduflow.user.User;
import com.eduflow.eduflow.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;  // ← Spring auto-configures this

    @KafkaListener(topics = "enrollment-events", groupId = "eduflow-group")
    public void handleEnrollmentEvent(String message) {  // ← String now
        try {
            EnrollmentEvent event = objectMapper.readValue(message, EnrollmentEvent.class);
            log.info("Received enrollment event for: {}", event.getStudentEmail());

            User user = userRepository.findById(event.getStudentId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Notification notification = Notification.builder()
                    .user(user)
                    .title("Welcome to " + event.getCourseTitle())
                    .message("You have successfully enrolled in " +
                            event.getCourseTitle() + ". Happy learning!")
                    .build();

            notificationRepository.save(notification);
            log.info("Notification saved for student: {}", event.getStudentEmail());

        } catch (Exception e) {
            log.error("Error processing enrollment event: {}", e.getMessage());
        }
    }
}