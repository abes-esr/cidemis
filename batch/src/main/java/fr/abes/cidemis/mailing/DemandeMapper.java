package fr.abes.cidemis.mailing;

import fr.abes.cidemis.constant.Constant;
import fr.abes.cidemis.model.cidemis.Commentaires;
import fr.abes.cidemis.model.cidemis.Demandes;
import fr.abes.cidemis.model.cidemis.PiecesJustificatives;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemandeMapper extends ModelMapper {
    /**
     * Fonction de mapping générique pour des listes
     *
     * @param source      Liste source
     * @param targetClass Classe des objets cibles
     * @return Liste des objets cibles
     */
    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> this.map(element, targetClass))
                .collect(Collectors.toList());
    }

    @Bean
    public void converterDemandes() {
        Converter<Demandes, DemandesDto> myConverter = new Converter<Demandes, DemandesDto>() {

            public DemandesDto convert(MappingContext<Demandes, DemandesDto> context) {
                Demandes demande = context.getSource();
                DemandesDto target = new DemandesDto(demande.getIdDemande());
                target.setDateDemande(demande.getDateDemande());
                target.setTypesDemandes(demande.getTypesDemandes().getIdTypeDemande());
                target.setZones(demande.getZones());
                target.setPays(demande.getNotice().getPays());
                target.setEmail(Constant.getCodePaysSorted().get(demande.getNotice().getPaysCIEPS()).getEmail());
                target.setCentreIssn(demande.getCentreissnCIEPS());
                target.setTypeRessourceContinue(demande.getNotice().getTypeRessourceShort());
                target.setTitre(demande.getTitre());
                target.setIssn(demande.getNotice().getIssn());
                target.setCommentaires(this.getCommentairesCSV(demande));
                target.setPpn(demande.getNotice().getPpn());
                target.setPiecesJustificatives(this.getPiecesJustificativesCSV(demande));
                target.setLiensNotice(this.getLiensNoticeCSV(demande));
                return target;
            }

            /**
             * Retourne les liens pour accéder aux détails de la notice
             *
             * @param demande la demande sous forme d'entité
             * @return les 4 liens avec le n de ppn
             */
            private String getLiensNoticeCSV(Demandes demande) {
                return
                        "http://janus.issn.org/cgi-bin/gw/issn_lists/cidemis_issn.pl?format=label&ppn=" + demande.getNotice().getPpn() + "\n" +
                                "http://janus.issn.org/cgi-bin/gw/issn_lists/cidemis_issn.pl?format=marc_iso&ppn=" + demande.getNotice().getPpn() + "\n" +
                                "http://janus.issn.org/cgi-bin/gw/issn_lists/cidemis_issn.pl?format=marc21_iso&ppn=" + demande.getNotice().getPpn() + "\n" +
                                "http://janus.issn.org/cgi-bin/gw/issn_lists/cidemis_issn.pl?format=marc_read&ppn=" + demande.getNotice().getPpn();
            }

            /**
             * Retourne la liste de pièces justificative sour forme de chaine de caractères
             *
             * @param demande entité demande
             * @return la liste de pièces justificatives sous forme de chaîne de caractères
             */
            private String getPiecesJustificativesCSV(Demandes demande) {
                StringBuilder pjcsv = new StringBuilder();
                List<PiecesJustificatives> listPj = demande.getPiecesJustificativeslist();
                if (listPj.size() > 0) {
                    for (PiecesJustificatives pj : listPj) {
                        pjcsv.append(pj.getUrlfichier() + "\n");
                    }
                }
                return pjcsv.toString();
            }

            /**
             * Retourne la liste de commentaires sous forme de chaine de caractères
             *
             * @param demande entité demande
             * @return un commentaire si l'ISSN de la demande est visible
             */
            private String getCommentairesCSV(Demandes demande) {
                StringBuilder commentairecsv = new StringBuilder();

                for (Commentaires comment : demande.getCommentairesList()) {
                    if (comment.getVisibleISSN())
                        commentairecsv.append(
                                "On " + comment.getDateCommentaireFormatee() + " by " + comment.getCbsUsers().getRoles().getLibRole() + " :" + "\n"
                                        + comment.getLibCommentaire() + "\n" + "\n"
                                        + "--------------------------------" + "\n" + "\n");
                }

                return commentairecsv.toString();
            }
        };
        this.addConverter(myConverter);
    }



}
