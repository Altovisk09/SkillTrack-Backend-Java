package com.eric.skilltrack.controller;

import com.eric.skilltrack.model.User;
import com.eric.skilltrack.model.enums.UserRole;
import com.eric.skilltrack.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService; //

    public UserController(UserService userService) { // <-- injeta pelo contrato
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() throws IOException {
        return userService.getAllUsers();
    }

    @GetMapping("/{ldap}")
    public Optional<User> getByLdap(@PathVariable String ldap) throws IOException {
        return userService.getByLdap(ldap);
    }

    @PostMapping("/register")
    public User registerUser(@RequestParam String ldap) throws IOException {
        return userService.registerUser(ldap);
    }

    @PutMapping("/{ldap}/role")
    public User updateRole(@PathVariable String ldap, @RequestParam UserRole role) throws IOException {
        return userService.updateRole(ldap, role);
    }

    @PutMapping("/{ldap}/last-session")
    public void updateLastSession(@PathVariable String ldap) throws IOException {
        userService.updateLastSession(ldap);
    }

    @PostMapping("/sync")
    public void syncFromHc() throws IOException {
        userService.syncFromHc();
    }
}
