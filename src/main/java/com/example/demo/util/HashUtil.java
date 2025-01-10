// src/main/java/com/example/demo/util/HashUtil.java
package com.example.demo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashUtil {

    private final String salt;

    public HashUtil(@Value("${anonymization.salt}") String salt) {
        this.salt = salt;
    }

    public String generateHash(String userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = userId + salt;
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte b : hashBytes){
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }
}
