package fr.abes.cidemis.model.cidemis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;

@Entity
@Table(name = "OPTIONS")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "SEQ_OPTIONS", sequenceName = "SEQ_OPTIONS", initialValue = 1, allocationSize = 1)
public class Options implements Serializable {
    private static final long serialVersionUID = 2217506004794521011L;

    @Id
    @Column(name = "ID_OPTION")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPTIONS")
    private Integer idOption;
    @ManyToOne
    @JoinColumn(name = "USER_NUM")
    private CbsUsers cbsUsers;
    @Column(name = "LIB_OPTION")
    private String libOption;
    @Column(name = "VALUE")
    private String value;
    @Column(name = "TYPE")
    private String type;
    @Transient
    private String name;
    @Transient
    private Integer position;
    @Transient
    private String typeInput;


    public Options(Options option) {
        this.idOption = option.idOption;
        this.cbsUsers = option.cbsUsers;
        this.libOption = option.libOption;
        this.value = option.value;
        this.type = option.type;
        this.name = option.name;
        this.position = option.position;
        this.typeInput = option.typeInput;
    }

    public Options(OptionsRoles option) {
        this.libOption = option.getLibOption();
        this.value = option.getValue();
        this.type = option.getType();
    }


    public class OptionNameComparator implements Comparator<Options> {
        @Override
        public int compare(Options o1, Options o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

}