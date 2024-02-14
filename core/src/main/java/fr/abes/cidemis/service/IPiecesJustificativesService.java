package fr.abes.cidemis.service;

import fr.abes.cidemis.model.cidemis.CbsUsers;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;

import java.util.List;

public interface IPiecesJustificativesService {
    Boolean save(PiecesJustificatives piecesJustificatives);

    Boolean delete(PiecesJustificatives piecesJustificatives);

    PiecesJustificatives findPiecesJustificatives(Integer idPiece);

    List<PiecesJustificatives> findAllPiecesJustificatives();

    List<PiecesJustificatives> findPiecesJustificativesByDemandes(Demandes demandes);

    List<PiecesJustificatives> findPiecesJustificativesByCbsUsers(CbsUsers cbsUsers);

    Integer getNbPiecesJustificativesByDemande(Demandes de);
}
