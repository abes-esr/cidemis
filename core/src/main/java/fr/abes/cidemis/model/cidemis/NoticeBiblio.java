package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table(name = "NOTICESBIBIO", schema = "AUTORITES")
@NoArgsConstructor
@Getter @Setter
public class NoticeBiblio implements Serializable {
    @Id
    @Column(name = "PPN")
    private String ppn;

    @Column(name = "DATE_ETAT")
    @Temporal(TemporalType.DATE)
    private Calendar dateEtat;

    public NoticeBiblio(String ppn, Calendar dateEtat) {
        this.dateEtat = dateEtat;
        this.ppn = ppn;
    }

}
