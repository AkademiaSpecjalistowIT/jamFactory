package pl.akademiaspecjalistowit.jamfactory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;

@Repository
public interface JamPlanProductionRepository extends JpaRepository<JamPlanProductionEntity, Long> {
}
