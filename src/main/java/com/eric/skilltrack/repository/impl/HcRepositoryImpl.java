package com.eric.skilltrack.repository.impl;

import com.eric.skilltrack.model.HC;
import com.eric.skilltrack.repository.HcRepository;
import com.eric.skilltrack.repository.GenericRepository; // <<--- Import correto
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class HcRepositoryImpl extends GenericRepository<HC> implements HcRepository {

    private static final String SHEET_NAME = "HC";

    public HcRepositoryImpl(Sheets sheetsService, @Value("${GOOGLE_SPREADSHEET_ID}") String spreadsheetId) {
        super(sheetsService, spreadsheetId);
    }

    @Override
    protected String sheetName() {
        return SHEET_NAME;
    }

    @Override
    protected HC fromRow(List<Object> row) {
        List<String> cols = new ArrayList<>();
        for (int i = 0; i < 30; i++) { // ajuste conforme colunas reais
            if (i < row.size() && row.get(i) != null) cols.add(String.valueOf(row.get(i)));
            else cols.add("");
        }

        HC hc = new HC();
        hc.setNome(cols.get(0));
        hc.setMatricula(cols.get(1));
        hc.setCargo(cols.get(2));
        hc.setEscala(cols.get(3));
        hc.setTurnoRes(cols.get(4));
        hc.setTurma(cols.get(5));
        hc.setEscalaTurnoTurma(cols.get(6));
        hc.setTurnoTurma(cols.get(7));
        hc.setStatus(cols.get(8));
        hc.setEmpresa(cols.get(9));
        hc.setAtividade(cols.get(10));
        hc.setAreaSOP(cols.get(11));
        hc.setProcesso(cols.get(12));
        hc.setDiretoIndireto(cols.get(13));
        hc.setGestorImediato(cols.get(14));
        hc.setGestao2(cols.get(15));
        hc.setGestao3(cols.get(16));
        hc.setAdmissao(cols.get(17));
        hc.setCriterios(cols.get(18));
        hc.setLdap(cols.get(19));
        hc.setIdGroot(cols.get(20));
        hc.setIdLenel(cols.get(21));
        hc.setFeriasInicio(cols.get(22));
        hc.setFeriasFim(cols.get(23));
        hc.setFimContrato(cols.get(24));
        hc.setDataLimite(cols.get(25));
        hc.setDataNascimento(cols.get(26));
        hc.setFretado(cols.get(27));
        hc.setAdmissaoOriginal(cols.get(28));
        return hc;
    }

    @Override
    protected List<Object> toRow(HC entity) {
        throw new UnsupportedOperationException("HC repository is read-only");
    }

    @Override
    public Optional<HC> findById(String id) throws IOException {
        return findByLdap(id);
    }

    @Override
    public Optional<HC> findByLdap(String ldap) throws IOException {
        int rowIndex = findRowIndexByColumn(sheetName(), "LDAP", ldap); // já vem do GenericRepository
        if (rowIndex == -1) return Optional.empty();
        List<Object> row = getRowValues(rowIndex);
        return Optional.of(fromRow(row));
    }

    @Override
    public List<HC> findAll() throws IOException {
        return findAllHC();
    }

    @Override
    protected Map<String, Object> toRowMap(HC entity) {
        return Map.of();
    }

    @Override
    public List<HC> findAllHC() throws IOException {
        List<List<Object>> rows = readAllData();
        List<HC> result = new ArrayList<>();
        for (List<Object> r : rows) result.add(fromRow(r));
        return result;
    }

    @Override
    public HC save(HC entity) throws IOException {
        throw new UnsupportedOperationException("HC repository is read-only");
    }

    @Override
    public HC update(HC entity) throws IOException {
        throw new UnsupportedOperationException("HC repository is read-only");
    }

    @Override
    public void deleteById(String id) throws IOException {
        throw new UnsupportedOperationException("HC repository is read-only");
    }
}
