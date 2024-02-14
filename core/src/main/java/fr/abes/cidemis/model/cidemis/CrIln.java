package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CR_ILN")
@Getter @Setter
public class CrIln implements Serializable {
    @Id
    @Column(name = "CR")
    String cr;
    @Column(name = "ILN")
    String iln;
}
