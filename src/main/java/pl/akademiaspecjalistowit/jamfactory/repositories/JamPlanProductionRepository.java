package pl.akademiaspecjalistowit.jamfactory.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;

@Repository
public interface JamPlanProductionRepository extends JpaRepository<JamPlanProductionEntity, Long> {
    List<JamPlanProductionEntity> findAllByPlanDate(LocalDate now);

    @Query("SELECT j.planDate, SUM(CAST(j.smallJamJars AS double) * :smallWeight), SUM(CAST(j.mediumJamJars AS double) * :mediumWeight), SUM(CAST(j.largeJamJars AS double) * :largeWeight) " +
            "FROM JamPlanProductionEntity j " +
            "WHERE j.planDate BETWEEN :dateFrom AND :dateTo " +
            "GROUP BY j.planDate " +
            "ORDER BY j.planDate DESC")
    List<Object[]> findAvailableProductionCapacityForSpecifiedPeriod(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("smallWeight") double smallWeight,
            @Param("mediumWeight") double mediumWeight,
            @Param("largeWeight") double largeWeight);

    default Map<LocalDate, List<Double>> findAvailableProductionCapacityForSpecifiedPeriodWithEnum(LocalDate dateFrom, LocalDate dateTo) {
        List<Object[]> rawResults = findAvailableProductionCapacityForSpecifiedPeriod(
                dateFrom, dateTo, JamPlanProductionEntity.getSmallWeight(), JamPlanProductionEntity.getMediumWeight(), JamPlanProductionEntity.getLargeWeight()

        );

        Map<LocalDate, List<Double>> resultMap = new HashMap<>();

        for (Object[] row : rawResults) {
            LocalDate date = (LocalDate) row[0];
            Double smallSum = (Double) row[1];
            Double mediumSum = (Double) row[2];
            Double largeSum = (Double) row[3];

            resultMap.put(date, Arrays.asList(smallSum, mediumSum, largeSum));
        }

        return resultMap;
    }

    List<JamPlanProductionEntity> findByPlanDate(LocalDate planDate);
}
