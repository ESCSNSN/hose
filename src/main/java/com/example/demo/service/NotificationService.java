package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;

    public void sendNotification(String userId, String title, String content) {
        String url = "/notification/" + userId;
        NotificationRequest notificationRequest = new NotificationRequest(title, content);
        restTemplate.postForObject(url, notificationRequest, Void.class);
    }

    private static class NotificationRequest {
        private String title;
        private String content;

        public NotificationRequest(String title, String content) {
            this.title = title;
            this.content = content;
        }

        // Getters and setters (if needed)
    }
}