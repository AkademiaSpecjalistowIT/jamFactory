package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.*;

@AllArgsConstructor
@Service

public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;
    private final ApiProperties apiProperties;

    @Override
    @Transactional
    public UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity = jamsMapper.toEntity(jamPlanProductionRequestDto);
        validateAddingNewProductionPlanPossibility(entity);
        jamPlanProductionRepository.save(entity);
        return entity.getPlanId();
    }

    private void validateAddingNewProductionPlanPossibility(JamPlanProductionEntity entity) {

        LocalDate newPlanDate = entity.getPlanDate();
        List<JamPlanProductionEntity> allByDate = jamPlanProductionRepository.findAllByPlanDate(newPlanDate);

        double totalActualSum = allByDate.stream()
                .mapToDouble(JamPlanProductionEntity::getTotalJamWeight)
                .sum();

        double newTotal = totalActualSum + entity.getTotalJamWeight();
        double maxProductionLimit = apiProperties.getMaxProductionLimit();

        System.out.println("Total weight before adding: " + totalActualSum);
        System.out.println("New plan weight: " + entity.getTotalJamWeight());
        System.out.println("Total weight after adding: " + newTotal);

        if (newTotal > maxProductionLimit) {
            double exceededLimit = newTotal - maxProductionLimit;
            checkAbilityDistributeExcessLimitToPreviousDays(exceededLimit, entity);
            //throw new ProductionException("Przekroczono limit produkcyjny o " + exceededLimit + " Kg");
        }
    }


    private void checkAbilityDistributeExcessLimitToPreviousDays(double exceededLimit, JamPlanProductionEntity entity) {

        Map<LocalDate, List<Double>> availableProductionCapacityForSpecifiedPeriod = jamPlanProductionRepository.
                findAvailableProductionCapacityForSpecifiedPeriodWithEnum(LocalDate.now(), entity.getPlanDate());

        fillMissingDates(availableProductionCapacityForSpecifiedPeriod, LocalDate.now(), entity.getPlanDate());
        for (Map.Entry<LocalDate, List<Double>> entry : availableProductionCapacityForSpecifiedPeriod.entrySet()) {

            if (entry.getValue().stream().mapToDouble(Double::doubleValue).sum() == 0) {
                entry.setValue(List.of(entity.getSmallJamJars() * JamPlanProductionEntity.getSmallWeight(),
                        entity.getMediumJamJars() * JamPlanProductionEntity.getMediumWeight(),
                        entity.getLargeJamJars() * JamPlanProductionEntity.getLargeWeight()));
            }
        }

        distributeAccordingPreviousPlans(availableProductionCapacityForSpecifiedPeriod, entity);
    }

    private void addDaysWithoutPlan(Map<LocalDate, List<Double>> availableProductionCapacityForSpecifiedPeriod, double remainderLkg, double remainderMkg, double remainderSkg) {

        for (Map.Entry<LocalDate, List<Double>> entry : availableProductionCapacityForSpecifiedPeriod.entrySet()) {

            List<JamPlanProductionEntity> byPlanDate = jamPlanProductionRepository.findByPlanDate(entry.getKey());
            if (!byPlanDate.isEmpty()) {
                continue;
            }

            double remainderProductionLimitKgThisDay = apiProperties.getMaxProductionLimit();
            double result = 0;
            int newJamPlanJamL = 0;
            int newJamPlanJamM = 0;
            int newJamPlanJamS = 0;

            if (remainderLkg > 0) {
                result = Math.min(remainderLkg, remainderProductionLimitKgThisDay);
                remainderProductionLimitKgThisDay = remainderProductionLimitKgThisDay - result;
                remainderLkg = remainderLkg - result;
                newJamPlanJamL = (int) Math.round(result / JamPlanProductionEntity.getLargeWeight());
            }

            if (remainderMkg > 0) {
                result = Math.min(remainderMkg, remainderProductionLimitKgThisDay);
                remainderProductionLimitKgThisDay = remainderProductionLimitKgThisDay - result;
                remainderMkg = remainderMkg - result;
                newJamPlanJamM = (int) Math.round(result / JamPlanProductionEntity.getMediumWeight());
            }

            if (remainderSkg > 0) {
                result = Math.min(remainderSkg, remainderProductionLimitKgThisDay);
                remainderProductionLimitKgThisDay = remainderProductionLimitKgThisDay - result;
                remainderSkg = remainderSkg - result;
                newJamPlanJamS = (int) Math.round(result / JamPlanProductionEntity.getSmallWeight());
            }
            if (newJamPlanJamL + newJamPlanJamM + newJamPlanJamS > 0) {
                JamPlanProductionEntity newJamPlanProductionEntity = new JamPlanProductionEntity(entry.getKey(), newJamPlanJamL, newJamPlanJamM, newJamPlanJamS);
                jamPlanProductionRepository.save(newJamPlanProductionEntity);
            }
        }
        if (remainderSkg + remainderMkg + remainderLkg > 0) {
            throw new ProductionException("Przekroczono limit produkcyjny o " + (remainderSkg + remainderMkg + remainderLkg) + " Kg");
        }
    }

    public static void fillMissingDates(Map<LocalDate, List<Double>> productionCapacity, LocalDate dateFrom, LocalDate dateTo) {

        LocalDate minDate = dateFrom;
        LocalDate maxDate = dateTo;

        LocalDate currentDate = minDate;
        while (!currentDate.isAfter(maxDate)) {
            productionCapacity.putIfAbsent(currentDate, new ArrayList<>());
            currentDate = currentDate.plusDays(1);
        }
    }

    private void distributeAccordingPreviousPlans(Map<LocalDate, List<Double>> availableProductionCapacityForSpecifiedPeriod, JamPlanProductionEntity jamPlanProductionEntity) {

        double remainderSkg = jamPlanProductionEntity.getSmallJamJars() * JamPlanProductionEntity.getSmallWeight();
        double remainderMkg = jamPlanProductionEntity.getMediumJamJars() * JamPlanProductionEntity.getMediumWeight();
        double remainderLkg = jamPlanProductionEntity.getLargeJamJars() * JamPlanProductionEntity.getLargeWeight();

        List<JamPlanProductionEntity> plansWithPlanDate = jamPlanProductionRepository.findByPlanDate(jamPlanProductionEntity.getPlanDate());
        double remainderProductionLimitKgThisPlan = apiProperties.getMaxProductionLimit() - plansWithPlanDate.stream().mapToDouble(JamPlanProductionEntity::getTotalJamWeight).sum();

        setupDefaultQuantity(jamPlanProductionEntity);
        if (remainderProductionLimitKgThisPlan > 0) {
            int round = (int) Math.round(Math.min(remainderLkg/ JamPlanProductionEntity.getLargeWeight(), remainderProductionLimitKgThisPlan) );
            jamPlanProductionEntity.setLargeJamJars(round);
            remainderLkg -= round;
            remainderProductionLimitKgThisPlan -= round;
        }
        if (remainderProductionLimitKgThisPlan > 0) {
            int round = (int) Math.round(Math.min(remainderMkg/ JamPlanProductionEntity.getMediumWeight(), remainderProductionLimitKgThisPlan) );
            jamPlanProductionEntity.setMediumJamJars(round);
            remainderMkg -= round;
            remainderProductionLimitKgThisPlan -= round;
        }
        if (remainderProductionLimitKgThisPlan > 0) {
            int round = (int) Math.round(Math.min(remainderSkg/ JamPlanProductionEntity.getSmallWeight(), remainderProductionLimitKgThisPlan) );
            jamPlanProductionEntity.setSmallJamJars(round);
            remainderSkg -= round;
            remainderProductionLimitKgThisPlan -= round;
        }

        double result = 0;
        if (remainderLkg + remainderMkg + remainderSkg <= 0) {
            return;
        }

        for (Map.Entry<LocalDate, List<Double>> entry : availableProductionCapacityForSpecifiedPeriod.entrySet()) {

            List<JamPlanProductionEntity> byPlanDate = jamPlanProductionRepository.findByPlanDate(entry.getKey());
            double remainderProductionLimitKgThisDay = apiProperties.getMaxProductionLimit()
                    - byPlanDate.stream().mapToDouble(JamPlanProductionEntity::getTotalJamWeight).sum()
                    - (jamPlanProductionEntity.getPlanDate().equals(entry.getKey()) ? jamPlanProductionEntity.getTotalJamWeight() : 0.0);
            if (remainderProductionLimitKgThisDay <= 0) {
                continue;
            }
            for (JamPlanProductionEntity entity : byPlanDate) {

                if (remainderLkg > 0) {
                    //result = distributeOneOrderL(entity, Math.min(remainderLkg, remainderProductionLimitKgThisDay));
                    result = distributeOneOrder(entity, Math.min(remainderLkg, remainderProductionLimitKgThisDay), "LAGRE");
                    remainderProductionLimitKgThisDay = remainderProductionLimitKgThisDay - result;
                    remainderLkg = remainderLkg - result;
                }

                if (remainderMkg > 0) {
                    //result = distributeOneOrderM(entity, Math.min(remainderMkg, remainderProductionLimitKgThisDay));
                    result = distributeOneOrder(entity, Math.min(remainderMkg, remainderProductionLimitKgThisDay), "MEDIUM");
                    remainderProductionLimitKgThisDay = remainderProductionLimitKgThisDay - result;
                    remainderMkg = remainderMkg - result;
                }

                if (remainderSkg > 0) {
                    //result = distributeOneOrderS(entity, Math.min(remainderSkg, remainderProductionLimitKgThisDay));
                    result = distributeOneOrder(entity, Math.min(remainderSkg, remainderProductionLimitKgThisDay), "SMALL");
                    remainderProductionLimitKgThisDay = remainderProductionLimitKgThisDay - result;
                    remainderSkg = remainderSkg - result;
                }
            }
        }
        if (remainderSkg + remainderMkg + remainderLkg > 0) {
            addDaysWithoutPlan(availableProductionCapacityForSpecifiedPeriod, remainderLkg, remainderMkg, remainderSkg);
        }
    }

    private static void setupDefaultQuantity(JamPlanProductionEntity jamPlanProductionEntity) {
        jamPlanProductionEntity.setSmallJamJars(0);
        jamPlanProductionEntity.setLargeJamJars(0);
        jamPlanProductionEntity.setMediumJamJars(0);
    }

    private double distributeOneOrderL(JamPlanProductionEntity jamPlanProductionEntity, double remainderKg) {

        jamPlanProductionEntity.setLargeJamJars(jamPlanProductionEntity.getLargeJamJars() + (int) Math.round(remainderKg / JamPlanProductionEntity.getLargeWeight()));
        return (int) Math.round(remainderKg / JamPlanProductionEntity.getLargeWeight());
    }

    private double distributeOneOrderM(JamPlanProductionEntity jamPlanProductionEntity, double remainderKg) {

        jamPlanProductionEntity.setMediumJamJars(jamPlanProductionEntity.getMediumJamJars() + (int) Math.round(remainderKg / JamPlanProductionEntity.getMediumWeight()));
        return (int) Math.round(remainderKg / JamPlanProductionEntity.getMediumWeight());
    }

    private double distributeOneOrderS(JamPlanProductionEntity jamPlanProductionEntity, double remainderKg) {

        jamPlanProductionEntity.setSmallJamJars(jamPlanProductionEntity.getSmallJamJars() + (int) Math.round(remainderKg / JamPlanProductionEntity.getSmallWeight()));
        return (int) Math.round(remainderKg / JamPlanProductionEntity.getSmallWeight());
    }

    private double distributeOneOrder(JamPlanProductionEntity jamPlanProductionEntity, double remainderKg, String jarSize) {
        double jarWeight;
        int currentJars;

        switch (jarSize) {
            case "LARGE":
                jarWeight = JamPlanProductionEntity.getLargeWeight();
                currentJars = jamPlanProductionEntity.getLargeJamJars();
                jamPlanProductionEntity.setLargeJamJars(currentJars + (int) Math.round(remainderKg / jarWeight));
                break;
            case "MEDIUM":
                jarWeight = JamPlanProductionEntity.getMediumWeight();
                currentJars = jamPlanProductionEntity.getMediumJamJars();
                jamPlanProductionEntity.setMediumJamJars(currentJars + (int) Math.round(remainderKg / jarWeight));
                break;
            case "SMALL":
                jarWeight = JamPlanProductionEntity.getSmallWeight();
                currentJars = jamPlanProductionEntity.getSmallJamJars();
                jamPlanProductionEntity.setSmallJamJars(currentJars + (int) Math.round(remainderKg / jarWeight));
                break;
            default:
                throw new IllegalArgumentException("Unexpected jar size: " + jarSize);
        }

        return (int) Math.round(remainderKg / jarWeight);
    }
}