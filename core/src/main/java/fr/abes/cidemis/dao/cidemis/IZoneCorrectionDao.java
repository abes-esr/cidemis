package fr.abes.cidemis.dao.cidemis;

import fr.abes.cidemis.model.cidemis.ZoneCorrection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IZoneCorrectionDao extends JpaRepository<ZoneCorrection, String> {
    @Query("select z from ZoneCorrection z order by z.zone")
    List<ZoneCorrection> findAllOrderByZone();
}
