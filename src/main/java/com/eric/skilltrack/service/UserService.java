package com.eric.skilltrack.service;

import com.eric.skilltrack.model.User;
import com.eric.skilltrack.model.enums.UserRole;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getByLdap(String ldap) throws IOException;

    List<User> getAllUsers() throws IOException;

    User registerUser(String ldap) throws IOException;

    User updateRole(String ldap, UserRole role) throws IOException;

    void updateLastSession(String ldap) throws IOException;

    void syncFromHc() throws IOException;
}
