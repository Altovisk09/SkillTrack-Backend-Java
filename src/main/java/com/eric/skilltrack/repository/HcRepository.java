package com.eric.skilltrack.repository;

import com.eric.skilltrack.model.HC;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface HcRepository extends BaseRepository<HC, String> {

    Optional<HC> findByLdap(String ldap) throws IOException;

    List<HC> findAllHC() throws IOException;
}
