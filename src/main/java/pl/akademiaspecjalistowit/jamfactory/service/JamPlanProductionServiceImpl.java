package pl.akademiaspecjalistowit.jamfactory.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.dto.JamJars;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

@AllArgsConstructor
@Service
@Slf4j
public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;
    private final ApiProperties apiProperties;

    @Override
    public UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity =
            jamsMapper.toEntity(jamPlanProductionRequestDto, apiProperties.getMaxProductionLimit());
        try {
            addNewProductionPlanForGivenDay(entity);
        } catch (ProductionException e) {
            log.info("Procesujemy dodanie planu prodyjnego na więcej niż 1 dzien, ponieważ : " + e.getMessage());
            addProductionPlanBeforeDeadline(entity);
        }
        return entity.getPlanId();
    }

    private void addNewProductionPlanForGivenDay(JamPlanProductionEntity newProductionPlan) {
        double maxProductionLimit = apiProperties.getMaxProductionLimit();
        double totalJamWeight = newProductionPlan.getTotalJamWeight();

        validateFittingNewProductionPlanInEmptyDay(maxProductionLimit, totalJamWeight);

        LocalDate newPlanDate = newProductionPlan.getPlanDate();
        JamPlanProductionEntity jamPlanProductionEntity = jamPlanProductionRepository.findByPlanDate(newPlanDate)
            .map(e -> updateExistingPlan(newProductionPlan, maxProductionLimit, totalJamWeight, e))
            .orElse(newProductionPlan);
        jamPlanProductionRepository.save(jamPlanProductionEntity);
    }

    private JamPlanProductionEntity updateExistingPlan(JamPlanProductionEntity newProductionPlan,
                                                       double maxProductionLimit, double totalJamWeight,
                                                       JamPlanProductionEntity e) {
        double newTotal = e.getTotalJamWeight() + newProductionPlan.getTotalJamWeight();
        if (newTotal > maxProductionLimit) {
            double exceededLimit = totalJamWeight - maxProductionLimit;
            throw new ProductionException(
                "Przekroczono limit produkcyjny na wskazany dzien o " + exceededLimit + " Kg");
        }
        e.updatePlan(newProductionPlan);
        return e;
    }

    private void validateFittingNewProductionPlanInEmptyDay(double maxProductionLimit, double totalJamWeight) {
        if (totalJamWeight > maxProductionLimit) {
            double exceededLimit = totalJamWeight - maxProductionLimit;
            throw new ProductionException(
                "Przekroczono limit produkcyjny na wskazany dzien o " + exceededLimit + " Kg");
        }
    }


    private void addProductionPlanBeforeDeadline(JamPlanProductionEntity newProductionPlan) {
        LocalDate today = LocalDate.now();

        List<JamPlanProductionEntity> allByPlanDateBetween =
            jamPlanProductionRepository.findAllByPlanDateBetween(today, newProductionPlan.getPlanDate());

        validateCapabilityForNewProductionPlan(newProductionPlan, today, allByPlanDateBetween);

        List<LocalDate> daySequenceDescending = createDaySequenceDescending(today, newProductionPlan.getPlanDate());

        JamJars jars = new JamJars(newProductionPlan.getSmallJamJars(), newProductionPlan.getMediumJamJars(),
            newProductionPlan.getLargeJamJars());

        for (LocalDate day : daySequenceDescending) {
            JamPlanProductionEntity jamPlanProductionEntity = allByPlanDateBetween.stream()
                .filter(e -> e.getPlanDate().equals(day))
                .findFirst()
                .map(e -> e.fillProductionPlan(jars))
                .orElseGet(() -> {
                    JamPlanProductionEntity jppe =
                        new JamPlanProductionEntity(day, apiProperties.getMaxProductionLimit());
                    jppe.fillProductionPlan(jars);
                    return jppe;
                });
            jamPlanProductionRepository.save(jamPlanProductionEntity);

            if (jars.isEmpty()) {
                break;
            }
        }
    }

    private List<LocalDate> createDaySequenceDescending(LocalDate today, LocalDate newProductionPlanDate) {
        //TODO
        return List.of();
    }

    private void validateCapabilityForNewProductionPlan(JamPlanProductionEntity newProductionPlan, LocalDate today,
                                                        List<JamPlanProductionEntity> allByPlanDateBetween) {
        long factoryWorkingDaysUntilDeadline = getFactoryWorkingDaysUntilDeadline(newProductionPlan, today);

        double maxProductionLimit = apiProperties.getMaxProductionLimit();
        double maxFactoryProductionInKg = factoryWorkingDaysUntilDeadline * maxProductionLimit;

        double plannedFactoryProductionInKg = allByPlanDateBetween.stream()
            .mapToDouble(JamPlanProductionEntity::getTotalJamWeight)
            .sum();

        if (plannedFactoryProductionInKg + newProductionPlan.getTotalJamWeight() > maxFactoryProductionInKg) {
            throw new ProductionException("Przekroczono limit produkcyjny fabryki do tego dnia");
        }
    }

    private long getFactoryWorkingDaysUntilDeadline(JamPlanProductionEntity entity, LocalDate today) {
        return Duration.between(today.atStartOfDay(),
            entity.getPlanDate().atStartOfDay()).toDays() + 1;
    }

}