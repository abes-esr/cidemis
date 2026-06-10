package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
