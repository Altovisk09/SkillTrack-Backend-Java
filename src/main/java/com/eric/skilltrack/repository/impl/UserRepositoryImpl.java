package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.User;
import com.eric.skilltrack.model.enums.UserRole;
import com.eric.skilltrack.repository.GenericRepository;
import com.eric.skilltrack.repository.HcRepository;
import com.eric.skilltrack.repository.UserRepository;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl extends GenericRepository<User> implements UserRepository {

    private static final String SHEET_NAME = "Usuarios";

    // Colunas (atenção: dependem da ordem real da planilha)
    private static final String COL_LDAP = "LDAP";
    private static final String COL_DATA_CAD = "Data_Cadastro";
    private static final String COL_ROLE = "Role";
    private static final String COL_ULTIMA_SESSAO = "Última_Sessão";
    private static final String COL_NOME = "Nome";
    private static final String COL_CARGO = "Cargo";
    private static final String COL_ESCALA = "Escala";
    private static final String COL_TURNO = "Turno";
    private static final String COL_STATUS = "Status";
    private static final String COL_EMPRESA = "Empresa";
    private static final String COL_AREA = "Área";
    private static final String COL_PROCESSO = "Processo";
    private static final String COL_GESTOR_IMEDIATO = "Gestor_Imediato";
    private static final String COL_GESTOR_2 = "Gestor_2";
    private static final String COL_GESTOR_3 = "Gestor_3";
    private static final String COL_ADMISSAO = "Admissão";

    public UserRepositoryImpl(
            Sheets sheetsService,
            @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId
    ) {
        super(sheetsService, spreadsheetId);
    }

    @Override
    protected String getSheetName() {
        return SHEET_NAME;
    }

    /**
     * Converte uma linha crua da planilha para um objeto User
     */
    @Override
    protected User fromRow(List<Object> row) {
        List<String> cols = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            cols.add(i < row.size() && row.get(i) != null ? String.valueOf(row.get(i)) : "");
        }

        User u = new User();
        u.setLdap(cols.get(0));
        u.setDataCadastro(cols.get(1));
        u.setRole(UserRole.fromString(cols.get(2)));
        u.setUltimaSessao(cols.get(3));
        u.setNome(cols.get(4));
        u.setCargo(cols.get(5));
        u.setEscala(cols.get(6));
        u.setTurno(cols.get(7));
        u.setStatus(cols.get(8));
        u.setEmpresa(cols.get(9));
        u.setArea(cols.get(10));
        u.setProcesso(cols.get(11));
        u.setGestorImediato(cols.get(12));
        u.setGestor2(cols.get(13));
        u.setGestor3(cols.get(14));
        u.setAdmissao(cols.get(15));
        return u;
    }


    /**
     * Converte um User para uma linha da planilha
     */
    @Override
    protected List<Object> toRow(User entity) {
        List<Object> row = new ArrayList<>();
        row.add(entity.getLdap());
        row.add(entity.getDataCadastro());
        row.add(entity.getRole() != null ? entity.getRole().name() : UserRole.PARTICIPANT.name());
        row.add(entity.getUltimaSessao());
        row.add(entity.getNome());
        row.add(entity.getCargo());
        row.add(entity.getEscala());
        row.add(entity.getTurno());
        row.add(entity.getStatus());
        row.add(entity.getEmpresa());
        row.add(entity.getArea());
        row.add(entity.getProcesso());
        row.add(entity.getGestorImediato());
        row.add(entity.getGestor2());
        row.add(entity.getGestor3());
        row.add(entity.getAdmissao());
        return row;
    }

    @Override
    public Optional<User> findById(String id) throws IOException {
        // findById é só um alias de findByLdap
        return findByLdap(id);
    }

    @Override
    public Optional<User> findByLdap(String ldap) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_LDAP, ldap);
        if (rowIndex == -1) return Optional.empty();
        List<Object> row = getRowValues(rowIndex);
        return Optional.of(fromRow(row));
    }

    @Override
    public List<User> findAll() throws IOException {
        return findAllUsers();
    }

    @Override
    protected Map<String, Object> toRowMap(User e) {
        // Monte o mapa usando os NOMES EXATOS dos cabeçalhos da aba "Usuarios"
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put(COL_LDAP,            e.getLdap());
        map.put(COL_DATA_CAD,        e.getDataCadastro());
        map.put(COL_ROLE,            e.getRole() != null ? e.getRole().name() : UserRole.PARTICIPANT.name());
        map.put(COL_ULTIMA_SESSAO,   e.getUltimaSessao());
        map.put(COL_NOME,            e.getNome());
        map.put(COL_CARGO,           e.getCargo());
        map.put(COL_ESCALA,          e.getEscala());
        map.put(COL_TURNO,           e.getTurno());
        map.put(COL_STATUS,          e.getStatus());
        map.put(COL_EMPRESA,         e.getEmpresa());
        map.put(COL_AREA,            e.getArea());
        map.put(COL_PROCESSO,        e.getProcesso());
        map.put(COL_GESTOR_IMEDIATO, e.getGestorImediato());
        map.put(COL_GESTOR_2,        e.getGestor2());
        map.put(COL_GESTOR_3,        e.getGestor3());
        map.put(COL_ADMISSAO,        e.getAdmissao());
        return map;
    }

    @Override
    public List<User> findAllUsers() throws IOException {
        List<List<Object>> rows = readAllData();
        List<User> result = new ArrayList<>();
        for (List<Object> r : rows) result.add(fromRow(r));
        return result;
    }

    @Override
    public User save(User entity) throws IOException {
        // Não sobrescreve se já existir
        Optional<User> exists = findByLdap(entity.getLdap());
        if (exists.isPresent()) return exists.get();

        // Defaults
        if (entity.getDataCadastro() == null || entity.getDataCadastro().isBlank()) {
            entity.setDataCadastro(LocalDate.now().toString());
        }
        if (entity.getRole() == null) {
            entity.setRole(UserRole.PARTICIPANT);
        }

        appendRow(toRow(entity));
        return entity;
    }

    @Override
    public User update(User entity) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_LDAP, entity.getLdap());
        if (rowIndex == -1) throw new IllegalArgumentException("User not found: " + entity.getLdap());
        updateRow(rowIndex, toRow(entity));
        return entity;
    }

    @Override
    public void deleteById(String id) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_LDAP, id);
        if (rowIndex == -1) throw new IllegalArgumentException("User not found: " + id);
        updateCell(rowIndex, COL_STATUS, "INACTIVE");
    }

    @Override
    public User registerIfNotExists(String ldap) throws IOException {
        Optional<User> existing = findByLdap(ldap);
        if (existing.isPresent()) return existing.get();

        User u = new User();
        u.setLdap(ldap);
        u.setDataCadastro(LocalDate.now().toString());
        u.setRole(UserRole.PARTICIPANT);

        appendRow(toRow(u));
        return u;
    }

    @Override
    public void updateLastSession(String ldap, String lastSession) throws IOException {
        int rowIndex = findRowIndexByColumn(getSheetName(), COL_LDAP, ldap);
        if (rowIndex == -1) throw new IllegalArgumentException("User not found: " + ldap);
        updateCell(rowIndex, COL_ULTIMA_SESSAO, lastSession);
    }

    @Override
    public void syncFromHc(HcRepository hcRepository) throws IOException {
        List<User> users = findAllUsers();
        for (User u : users) {
            hcRepository.findByLdap(u.getLdap()).ifPresent(h -> {
                // Atualiza dados dinâmicos vindos da aba HC
                u.setNome(h.getNome());
                u.setCargo(h.getCargo());
                u.setEscala(h.getEscala());
                u.setTurno(h.getTurnoRes());
                u.setStatus(h.getStatus());
                u.setEmpresa(h.getEmpresa());
                u.setArea(h.getAreaSOP());
                u.setProcesso(h.getProcesso());
                u.setGestorImediato(h.getGestorImediato());
                u.setGestor2(h.getGestao2());
                u.setGestor3(h.getGestao3());
                u.setAdmissao(h.getAdmissao());

                try {
                    update(u); // só atualiza se realmente existir no HC
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao atualizar usuário: " + u.getLdap(), e);
                }
            });
        }
    }

    @Override
    public List<User> saveAll(List<User> entities) throws IOException {
        return List.of();
    }
}