package fr.abes.cidemis.service.impl;

import fr.abes.cidemis.dao.cidemis.CidemisDaoProvider;
import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import fr.abes.cidemis.service.IPiecesJustificativesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PiecesJustificativesService implements IPiecesJustificativesService {
    @Autowired
    private CidemisDaoProvider dao;

    /**
     *
     * Services de la table Pieces_Justificatives
     */
    @Override
    public Boolean save(PiecesJustificatives piecesJustificatives) {
        if (piecesJustificatives == null) {
            log.error(
                    "Erreur dans le parametre d'entrée , la variable 'pieces_justificatives' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getPiecesJustificativesDao().save(piecesJustificatives);
            log.info( "Sauvegarde: Pieces_Justificatives. Id_piece:"
                        + piecesJustificatives.getIdPiece().toString() + " OK");
            return true;
        } catch (Exception ex) {
            log.error(
                    "function save(). Pieces_Justificatives. Id_piece:" + piecesJustificatives.getIdPiece().toString(),
                    ex);
            return false;
        }
    }

    @Override
    public Boolean delete(PiecesJustificatives piecesJustificatives) {
        if (piecesJustificatives == null) {
            log.error(
                    "Erreur dans le parametre d'entrée , la variable 'pieces_justificatives' vaut 'null'.");
            return false;
        }

        try {
            this.dao.getPiecesJustificativesDao().delete(piecesJustificatives);
            return true;
        } catch (Exception ex) {
            log.error( "function delete(). Pieces_Justificatives. Id_piece:"
                    + piecesJustificatives.getIdPiece().toString(), ex);
            return false;
        }
    }

    @Override
    public PiecesJustificatives findPiecesJustificatives(Integer idPiece) {
        Optional<PiecesJustificatives> piecesJustificatives = this.dao.getPiecesJustificativesDao().findById(idPiece);

        if (!piecesJustificatives.isPresent())
            log.warn(
                    "La fonction 'findPieces_Justificatives' n'a retournée aucun résultat. Id_piece:" + idPiece);
        return piecesJustificatives.get();
    }

    @Override
    public List<PiecesJustificatives> findAllPiecesJustificatives() {
        List<PiecesJustificatives> piecesJustificativesList = this.dao.getPiecesJustificativesDao()
                .findAll();

        if (piecesJustificativesList.isEmpty())
            log.debug( "La fonction 'findAllPieces_Justificatives' n'a retournée aucun résultat.");

        return piecesJustificativesList;
    }

    @Override
    public List<PiecesJustificatives> findPiecesJustificativesByDemandes(Demandes demandes) {
        List<PiecesJustificatives> piecesJustificativesList = this.dao.getPiecesJustificativesDao()
                .findAllByDemande(demandes);

        if (piecesJustificativesList.isEmpty())
            log.debug(
                    "La fonction 'findPieces_JustificativesByDemandes' n'a retournée aucun résultat. Id_demande:"
                            + demandes.getIdDemande().toString());

        return piecesJustificativesList;
    }

    @Override
    public List<PiecesJustificatives> findPiecesJustificativesByCbsUsers(CbsUsers cbsUsers) {
        List<PiecesJustificatives> piecesJustificativesList = this.dao.getPiecesJustificativesDao()
                .findAllByCbsUsers(cbsUsers);

        if (piecesJustificativesList.isEmpty())
            log.debug(
                    "La fonction 'findPieces_JustificativesByCbs_Users' n'a retournée aucun résultat. User_num:"
                            + cbsUsers.getUserNum().toString());

        return piecesJustificativesList;
    }

    @Override
    public Integer getNbPiecesJustificativesByDemande(Demandes de) {
        return this.dao.getPiecesJustificativesDao().countAllByDemande(de);
    }
}
