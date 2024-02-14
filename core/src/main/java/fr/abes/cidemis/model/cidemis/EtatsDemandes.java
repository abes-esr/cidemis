package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "ETATSDEMANDES")
@NoArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "SEQ_ETATSDEMANDES", sequenceName = "SEQ_ETATSDEMANDES", initialValue = 1, allocationSize = 1)
public class EtatsDemandes implements Serializable {
    private static final long serialVersionUID = 8278430402804358629L;

    @Id
    @Column(name = "ID_ETATDEMANDE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ETATSDEMANDES")
    private Integer idEtatDemande;
    @Column(name = "LIBELLE_ETATDEMANDE")
    private String libelleEtatDemande;
    @OneToMany(mappedBy = "etatsDemandes")
    private List<Demandes> demandesList;
    @OneToMany(mappedBy = "etatsdemandes")
    private List<JournalDemandes> journalDemandesList;


 }