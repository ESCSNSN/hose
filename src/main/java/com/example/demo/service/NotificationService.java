package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;

    public void sendNotification(String userId, String title, String content) {
        String url = "https://rmation-chat.kro.kr/notification/" + userId;
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

        // Getter와 Setter
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}