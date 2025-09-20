package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, String> {

    Optional<User> findByLdap(String ldap) throws IOException;

    List<User> findAllUsers() throws IOException;

    User registerIfNotExists(String ldap) throws IOException;

    void updateLastSession(String ldap, String lastSession) throws IOException;

    // Se for usar HC (Human Capital), mantemos esse método
    void syncFromHc(HcRepository hcRepository) throws IOException;
}
