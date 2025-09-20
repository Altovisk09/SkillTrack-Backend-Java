package com.eric.skilltrack.controller;

import com.eric.skilltrack.model.User;
import com.eric.skilltrack.model.enums.UserRole;
import com.eric.skilltrack.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl UserServiceImpl;

    public UserController(UserServiceImpl UserServiceImpl) {
        this.UserServiceImpl = UserServiceImpl;
    }

    // GET /users -> lista todos
    @GetMapping
    public List<User> getAllUsers() throws IOException {
        return UserServiceImpl.getAllUsers();
    }

    // GET /users/{ldap} -> busca por LDAP
    @GetMapping("/{ldap}")
    public Optional<User> getByLdap(@PathVariable String ldap) throws IOException {
        return UserServiceImpl.getByLdap(ldap);
    }

    // POST /users/register?ldap=...
    @PostMapping("/register")
    public User registerUser(@RequestParam String ldap) throws IOException {
        return UserServiceImpl.registerUser(ldap);
    }

    // PUT /users/{ldap}/role?role=TRAINER
    @PutMapping("/{ldap}/role")
    public User updateRole(@PathVariable String ldap, @RequestParam UserRole role) throws IOException {
        return UserServiceImpl.updateRole(ldap, role);
    }

    // PUT /users/{ldap}/last-session
    @PutMapping("/{ldap}/last-session")
    public void updateLastSession(@PathVariable String ldap) throws IOException {
        UserServiceImpl.updateLastSession(ldap);
    }

    // POST /users/sync -> força sync manual (além do @Scheduled)
    @PostMapping("/sync")
    public void syncFromHc() throws IOException {
        UserServiceImpl.syncFromHc();
    }
}
