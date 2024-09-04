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
            "HAVING SUM(CAST(j.smallJamJars AS double) * :smallWeight) <= :neededSmallWeight " +
            "AND SUM(CAST(j.mediumJamJars AS double) * :mediumWeight) <= :neededMediumWeight " +
            "AND SUM(CAST(j.largeJamJars AS double) * :largeWeight) <= :neededLargeWeight " +
            "ORDER BY j.planDate DESC")

//    @Query("SELECT * FROM (" +
//            "SELECT d.planDate, 0 AS totalSmallWeight, 0 AS totalMediumWeight, 0 AS totalLargeWeight " +
//            "FROM (SELECT DISTINCT j.planDate " +
//            "      FROM JamPlanProductionEntity j " +
//            "      WHERE j.planDate BETWEEN :dateFrom AND :dateTo) d " +
//            "LEFT JOIN JamPlanProductionEntity jp ON d.planDate = jp.planDate " +
//            "WHERE jp.planDate IS NULL " +
//            "UNION " +
//            "SELECT j.planDate, " +
//            "SUM(CAST(j.smallJamJars AS double) * :smallWeight) AS totalSmallWeight, " +
//            "SUM(CAST(j.mediumJamJars AS double) * :mediumWeight) AS totalMediumWeight, " +
//            "SUM(CAST(j.largeJamJars AS double) * :largeWeight) AS totalLargeWeight " +
//            "FROM JamPlanProductionEntity j " +
//            "WHERE j.planDate BETWEEN :dateFrom AND :dateTo " +
//            "GROUP BY j.planDate " +
//            "HAVING SUM(CAST(j.smallJamJars AS double) * :smallWeight) <= :neededSmallWeight " +
//            "AND SUM(CAST(j.mediumJamJars AS double) * :mediumWeight) <= :neededMediumWeight " +
//            "AND SUM(CAST(j.largeJamJars AS double) * :largeWeight) <= :neededLargeWeight) AS result " +
//            "ORDER BY planDate DESC")
    List<Object[]> findAvailableProductionCapacityForSpecifiedPeriod(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("smallWeight") double smallWeight,
            @Param("mediumWeight") double mediumWeight,
            @Param("largeWeight") double largeWeight,
            @Param("neededSmallWeight") int neededSmallWeight,
            @Param("neededMediumWeight") int neededMediumWeight,
            @Param("neededLargeWeight") int neededLargeWeight);

    default Map<LocalDate, List<Double>> findAvailableProductionCapacityForSpecifiedPeriodWithEnum(LocalDate dateFrom, LocalDate dateTo,
                                                                                                   int neededSmallWeight, int neededMediumWeight, int neededLargeWeight) {
        List<Object[]> rawResults = findAvailableProductionCapacityForSpecifiedPeriod(
                dateFrom, dateTo, JamPlanProductionEntity.getSmallWeight(), JamPlanProductionEntity.getMediumWeight(), JamPlanProductionEntity.getLargeWeight(),
                neededSmallWeight, neededMediumWeight, neededLargeWeight
        );

        // Преобразование результата в Map<LocalDate, List<Double>>
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
