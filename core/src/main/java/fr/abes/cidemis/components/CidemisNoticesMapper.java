package fr.abes.cidemis.components;

import fr.abes.cidemis.model.dto.CidemisNoticesDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CidemisNoticesMapper implements RowMapper<CidemisNoticesDto> {
    @Override
    public CidemisNoticesDto mapRow(ResultSet resultSet, int i) throws SQLException {
        CidemisNoticesDto cidemisNoticesDto = new CidemisNoticesDto();
        cidemisNoticesDto.setPpn(resultSet.getString("PPN"));
        cidemisNoticesDto.setIssn(resultSet.getString("NOTICE_ISSN"));
        cidemisNoticesDto.setTitre(setTitre(resultSet));
        cidemisNoticesDto.setTitreA(resultSet.getString("TITRE_A"));
        cidemisNoticesDto.setTitreE(resultSet.getString("TITRE_E"));
        cidemisNoticesDto.setTitreC(resultSet.getString("TITRE_C"));
        cidemisNoticesDto.setTitreH(resultSet.getString("TITRE_H"));
        cidemisNoticesDto.setTitreI(resultSet.getString("TITRE_I"));
        cidemisNoticesDto.setTitreCle(resultSet.getString("TITRE_CLE"));
        cidemisNoticesDto.setTitreCleAbrege(resultSet.getString("TITRE_ABREGE"));
        cidemisNoticesDto.setPays(resultSet.getString("PAYS"));
        cidemisNoticesDto.setDateDebut(resultSet.getString("DATE_DEBUT"));
        cidemisNoticesDto.setDateFin(resultSet.getString("DATE_FIN"));
        cidemisNoticesDto.setTypeDocument(resultSet.getString("TYPE_DOCUMENT"));
        cidemisNoticesDto.setStatutNotice(resultSet.getString("STATUT_NOTICE"));
        cidemisNoticesDto.setCentreISSN(resultSet.getString("NOTICE_CENTRE_ISSN"));
        cidemisNoticesDto.setIssn035(resultSet.getString("ISSN035"));
        cidemisNoticesDto.setFrbnf(resultSet.getString("FRBNF"));
        cidemisNoticesDto.setTypeDeRessourceContinue(resultSet.getString("TYPE_RESSOURCE_CONTINUE"));
        cidemisNoticesDto.setSourceDeCatalogageISSN(resultSet.getString("SOURCE_CATALOGUAGE_ISSN"));
        cidemisNoticesDto.setNoteIdentifiants(resultSet.getString("NOTE_IDENTIFIANTS"));
        cidemisNoticesDto.setNoteGeneraleCatalogueur(resultSet.getString("NOTE_GENERALE_CATALOGUEUR"));

        return cidemisNoticesDto;
    }

    private String setTitre(ResultSet rs) throws SQLException {
        StringBuilder titre = new StringBuilder(rs.getString("TITRE_A")!=null?rs.getString("TITRE_A"):"");
        if (rs.getString("TITRE_E")!=null)
            titre.append(" " +rs.getString("TITRE_E"));
        if (rs.getString("TITRE_C")!=null)
            titre.append(" " +rs.getString("TITRE_C"));
        if (rs.getString("TITRE_H")!=null)
            titre.append(" " +rs.getString("TITRE_H"));
        if (rs.getString("TITRE_I")!=null)
            titre.append(" " +rs.getString("TITRE_I"));
        return titre.toString();
    }

}
