package com.eric.skilltrack.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {
    List<T> findAll(String sheetName);
    Optional<T> findById(String sheetName, String id);
    void save(String sheetName, T entity);
    void update(String sheetName, String id, T entity);
    void delete(String sheetName, String id);
}
