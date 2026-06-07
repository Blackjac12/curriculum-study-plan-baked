package com.example.Integration.project.security;

import com.example.Integration.project.entity.User;
import com.example.Integration.project.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepo;

    public UserDetailsServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = user.getRoleAssignments() == null ? List.of() : user.getRoleAssignments().stream()
                .map(ra -> new SimpleGrantedAuthority("ROLE_" + ra.getRole().getName().toUpperCase()))
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}

