package pl.akademiaspecjalistowit.jamfactory.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface JamPlanProductionRepository extends JpaRepository<JamPlanProductionEntity, Long> {
    List<JamPlanProductionEntity> findAllByPlanDate(LocalDate now);

    List<JamPlanProductionEntity> findAllByPlanDateBetween(LocalDate from, LocalDate to);

    Optional<JamPlanProductionEntity> findByPlanDate(LocalDate planDate);
}
