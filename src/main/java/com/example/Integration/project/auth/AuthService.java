package com.example.Integration.project.auth;

import com.example.Integration.project.entity.User;
import com.example.Integration.project.repository.UserRepository;
import com.example.Integration.project.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authManager, UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(String email, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userRepo.findByEmailIgnoreCase(email).orElseThrow(() -> new RuntimeException("User not found"));

        // collect roles from role assignments
        List<String> roles = user.getRoleAssignments() == null ? List.of() : user.getRoleAssignments().stream()
                .map(ra -> "ROLE_" + ra.getRole().getName().toUpperCase())
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getEmail(), roles);
        LoginResponse resp = new LoginResponse();
        resp.setAccessToken(token);
        resp.setExpiresIn(jwtUtil.getExpirationSeconds());
        resp.setRoles(roles);
        return resp;
    }
}
