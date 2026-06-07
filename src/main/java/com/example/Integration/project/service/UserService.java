package com.example.Integration.project.service;

import com.example.Integration.project.entity.User;
import com.example.Integration.project.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final VerificationService verificationService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom rnd = new SecureRandom();

    public UserService(UserRepository repo,
                       VerificationService verificationService,
                       MailService mailService,
                       PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.verificationService = verificationService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    private String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Transactional
    public String register(String name, String email) {
        String normalizedEmail = normalize(email);
        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email requerido");
        }

        Optional<User> existingOpt = repo.findByEmailIgnoreCase(normalizedEmail);
        if (existingOpt.isPresent()) {
            User u = existingOpt.get();
            if (u.isVerified()) {
                return "EXISTS_VERIFIED";
            } else {
                String code = verificationService.generateCode(u.getEmail());
                mailService.sendVerificationEmail(u.getEmail(), code);
                return "EXISTS_NOT_VERIFIED";
            }
        }

        User user = new User();
        user.setName(name == null ? "" : name.trim());
        user.setEmail(normalizedEmail);
        user.setVerified(false);
        user.setCreatedAt(OffsetDateTime.now());

        try {
            repo.save(user);
        } catch (DataIntegrityViolationException ex) {
            // race condition: otro proceso insertó el mismo email
            return "CONFLICT";
        }

        String code = verificationService.generateCode(normalizedEmail);
        mailService.sendVerificationEmail(normalizedEmail, code);
        return "SENT";
    }

    @Transactional
    public String login(String email, String password) {
        String normalized = normalize(email);
        User user = repo.findByEmailIgnoreCase(normalized).orElse(null);
        if (user == null) return "NOT_FOUND";
        if (!user.isVerified()) return "NOT_VERIFIED";
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "INVALID_PASSWORD";
        }
        return "SUCCESS";
    }

    @Transactional
    public String verify(String email, String code) {
        String normalized = normalize(email);
        boolean ok = verificationService.verifyCode(normalized, code);
        if (!ok) return "INVALID_OR_EXPIRED";

        User user = repo.findByEmailIgnoreCase(normalized).orElseThrow(() -> new IllegalStateException("User not found"));
        if (user.isVerified()) return "ALREADY_VERIFIED";

        String provisional = generateRandomPassword(10);
        String hashed = passwordEncoder.encode(provisional);

        user.setPassword(hashed);
        user.setVerified(true);
        repo.save(user);

        mailService.sendProvisionalPassword(normalized, provisional);
        return "VERIFIED_AND_SENT_PROVISIONAL";
    }

    /**
     * Cambiar contraseña: el usuario pasa email + provisional + nueva.
     */
    @Transactional
    public String setPassword(String email, String provisional, String newPassword) {
        String normalized = normalize(email);
        User user = repo.findByEmailIgnoreCase(normalized).orElse(null);
        if (user == null) return "NOT_FOUND";

        if (!passwordEncoder.matches(provisional, user.getPassword())) {
            return "PROVISIONAL_INVALID";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
        return "PASSWORD_UPDATED";
    }

    private String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public Object getAll() {
        return repo.findAll();
    }

    public void delete(String userId) {
        User u = repo.findById(Long.valueOf(userId)).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + userId));
        repo.delete(u);
    }

    public String changePassword(String email, String provisional, String newPassword) {
        String normalized = normalize(email);
        User user = repo.findByEmailIgnoreCase(normalized).orElse(null);
        if (user == null) return "NOT_FOUND";

        if (!passwordEncoder.matches(provisional, user.getPassword())) {
            return "PROVISIONAL_INVALID";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        repo.save(user);
        return "SUCCESS";
    }
}