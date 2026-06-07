package com.example.Integration.project.controller;

import com.example.Integration.project.entity.Role;
import com.example.Integration.project.entity.RoleAssignment;
import com.example.Integration.project.entity.User;
import com.example.Integration.project.repository.RoleAssignmentRepository;
import com.example.Integration.project.repository.RoleRepository;
import com.example.Integration.project.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role-assignments")
@CrossOrigin
public class RoleAssignmentController {

    private final RoleAssignmentRepository repo;
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;

    public RoleAssignmentController(RoleAssignmentRepository repo, UserRepository userRepo, RoleRepository roleRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @GetMapping("/all")
    public List<RoleAssignment> getAll() {
        return repo.findAll();
    }

    @PostMapping("/add")
    public RoleAssignment create(@RequestBody RoleAssignment ra) {
        User user = userRepo.findById(ra.getUser().getId()).orElseThrow();
        Role role = roleRepo.findById(ra.getRole().getId()).orElseThrow();

        ra.setUser(user);
        ra.setRole(role);

        return repo.save(ra);
    }

    @PutMapping("/update/{id}" )
    public RoleAssignment update(@PathVariable Long id, @RequestBody RoleAssignment raDetails) {
        RoleAssignment ra = repo.findById(id).orElseThrow();
        User user = userRepo.findById(raDetails.getUser().getId()).orElseThrow();
        Role role = roleRepo.findById(raDetails.getRole().getId()).orElseThrow();

        ra.setUser(user);
        ra.setRole(role);
        return repo.save(ra);
    }

}
