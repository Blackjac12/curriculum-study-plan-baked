package com.example.Integration.project.security;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private final JwtProperties props;
    private final Key key;

    public JwtUtil(JwtProperties props) {
        this.props = props;
        if (props.getSecret() == null || props.getSecret().isBlank()) {
            throw new IllegalStateException("JWT secret must be set in environment variable jwt.secret");
        }
        // use Keys class from jjwt at runtime
        this.key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(props.getSecret().getBytes());
    }

    public String generateToken(String subject, List<String> roles) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date exp = new Date(now + props.getExpirationSeconds() * 1000L);

        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(subject)
                .setIssuer(props.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(exp)
                .claim("roles", roles)
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    /**
     * Valida token y devuelve las claims como Map (no expone tipos de jjwt en la firma pública).
     */
    public Map<String, Object> parseClaims(String token) {
        try {
            io.jsonwebtoken.Jws<io.jsonwebtoken.Claims> jws = io.jsonwebtoken.Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            io.jsonwebtoken.Claims claims = jws.getBody();
            return claims;
        } catch (Exception ex) {
            // lanzar RuntimeException para simplificar (no referenciamos JwtException en firma)
            throw new RuntimeException("Invalid JWT token", ex);
        }
    }

    public String getUsername(String token) {
        Map<String, Object> claims = parseClaims(token);
        Object sub = claims.get("sub");
        return sub != null ? sub.toString() : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Map<String, Object> claims = parseClaims(token);
        Object r = claims.get("roles");
        if (r instanceof List) {
            return ((List<Object>) r).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    public long getExpirationSeconds() {
        return props.getExpirationSeconds();
    }
}
