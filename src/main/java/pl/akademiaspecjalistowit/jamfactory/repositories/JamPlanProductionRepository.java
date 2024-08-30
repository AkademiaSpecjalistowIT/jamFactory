package pl.akademiaspecjalistowit.jamfactory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JamPlanProductionRepository extends JpaRepository<JamPlanProductionEntity, Long> {
   List<JamPlanProductionEntity> findAllByPlanDate(LocalDate now);
}
