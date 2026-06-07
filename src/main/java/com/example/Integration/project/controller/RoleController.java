package com.example.Integration.project.controller;

import com.example.Integration.project.entity.Role;
import org.springframework.web.bind.annotation.*;
import com.example.Integration.project.repository.RoleRepository;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleRepository repo;

    public RoleController(RoleRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/all")
    public List<Role> getAll() {
        return repo.findAll();
    }

    @PostMapping("/add")
    public Role create(@RequestBody Role r) {
        return repo.save(r);
    }
}

