package com.example.Integration.project.service;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {

    private record VerificationData(String code, OffsetDateTime expiresAt) {}

    private final Map<String, VerificationData> codes = new ConcurrentHashMap<>();

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public String generateCode(String email) {
        String normalized = normalize(email);
        String code = String.format("%06d", (int)(Math.random() * 1_000_000));
        codes.put(normalized, new VerificationData(code, OffsetDateTime.now().plusMinutes(5)));
        return code;
    }

    public boolean verifyCode(String email, String code) {
        String normalized = normalize(email);
        VerificationData data = codes.get(normalized);
        if (data == null) return false;
        if (OffsetDateTime.now().isAfter(data.expiresAt)) {
            codes.remove(normalized);
            return false;
        }
        boolean valid = data.code.equals(code);
        if (valid) codes.remove(normalized);
        return valid;
    }
}

