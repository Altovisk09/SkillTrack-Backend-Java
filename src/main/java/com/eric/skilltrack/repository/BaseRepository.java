package com.eric.skilltrack.repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Interface base para todos os repositórios.
 * Define operações CRUD genéricas sobre entidades T, identificadas por ID.
 */
public interface BaseRepository<T, ID> {

    /**
     * Busca uma entidade pelo seu ID.
     * @param id identificador (ex.: LDAP do usuário).
     * @return Optional contendo a entidade se encontrada.
     */
    Optional<T> findById(ID id) throws IOException;

    /**
     * Retorna todas as entidades da aba correspondente.
     */
    List<T> findAll() throws IOException;

    /**
     * Salva uma nova entidade (append).
     */
    T save(T entity) throws IOException;

    /**
     * Atualiza a entidade existente.
     */
    T update(T entity) throws IOException;

    /**
     * Exclui (ou marca como inativo) a entidade pelo ID.
     */
    void deleteById(ID id) throws IOException;

    /**
     * Busca o número da linha onde a coluna == valor.
     * @param sheetName nome da aba.
     * @param columnName nome da coluna no header.
     * @param value valor a ser comparado.
     * @return índice da linha na planilha (1-based). -1 se não encontrar.
     */
    int findRowIndexByColumn(String sheetName, String columnName, String value) throws IOException;
}
