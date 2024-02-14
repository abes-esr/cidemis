package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.CrIln;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICrIlnDao extends JpaRepository<CrIln, String> {
    Optional<CrIln> getCrIlnByIln(String iln);

    CrIln findAllByCr(String cr);
}
