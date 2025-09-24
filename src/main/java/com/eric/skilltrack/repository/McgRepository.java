package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.GeneralControlMultipliers;
import com.eric.skilltrack.model.User;

import java.io.IOException;

public interface McgRepository extends BaseRepository<GeneralControlMultipliers, String>{
    void insertBasicTrainerData(User user) throws IOException;

    boolean existsByLdap(String ldap) throws IOException;
}
