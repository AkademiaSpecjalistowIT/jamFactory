package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.model.JamJars;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ProductionPlanBeforeDeadlineImpl implements ProductionPlanBeforeDeadline{
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final ApiProperties apiProperties;

    @Override
    public void addProductionPlanBeforeDeadline(JamPlanProductionEntity newProductionPlan) {
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
        List<LocalDate> dates = new ArrayList<>();

        while (!today.isAfter(newProductionPlanDate)) {
            dates.add(today);
            today = today.plusDays(1);
        }
        return dates;
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
