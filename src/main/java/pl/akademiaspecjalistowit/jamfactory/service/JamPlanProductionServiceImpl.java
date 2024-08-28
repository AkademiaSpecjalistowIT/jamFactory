package pl.akademiaspecjalistowit.jamfactory.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;
    private final ApiProperties apiProperties;

    @Transactional
    @Override
    public UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        validateProductionPlan(jamPlanProductionRequestDto);
        JamPlanProductionEntity entity = jamsMapper.toEntity(jamPlanProductionRequestDto);
        jamPlanProductionRepository.save(entity);
        return entity.getPlanId();
    }

    private void validateProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        LocalDate today = LocalDate.now();
        LocalDate plannedPlanDate = jamPlanProductionRequestDto.getPlanDate();
        LocalDate checkUntilDate;
        Integer maxDeliveryCapacityPerDay = apiProperties.getMaxDeliveryCapacity();

        Integer totalJamJarsForPlannedDay = jamPlanProductionRequestDto.getSmallJamJars() +
                jamPlanProductionRequestDto.getMediumJamJars() +
                jamPlanProductionRequestDto.getLargeJamJars();

        List<JamPlanProductionEntity> existingPlans = jamPlanProductionRepository.findAll();

        LocalDate latestPlanDate = existingPlans.stream()
                .map(JamPlanProductionEntity::getPlanDate)
                .max(LocalDate::compareTo)
                .orElse(plannedPlanDate);

        if (plannedPlanDate.isAfter(latestPlanDate)) {
            checkUntilDate = plannedPlanDate;
        } else {
            checkUntilDate = latestPlanDate;
        }

        Integer totalJarsFromTodayToCheckDate = existingPlans.stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(checkUntilDate))
                .mapToInt(plan -> plan.getSmallJamJars() + plan.getMediumJamJars() + plan.getLargeJamJars())
                .sum();

        if (!plannedPlanDate.isBefore(today) && !plannedPlanDate.isAfter(checkUntilDate)) {
            totalJarsFromTodayToCheckDate += totalJamJarsForPlannedDay;
        }

        long daysInRange = Duration.between(today.atStartOfDay(), checkUntilDate.atStartOfDay()).toDays() + 1;
        long maxCapacityInRange = daysInRange * maxDeliveryCapacityPerDay;

        if (totalJarsFromTodayToCheckDate > maxCapacityInRange) {
            throw new ProductionException("Przekraczajaca zdolnośc transportowa na period z " + today + " po " + checkUntilDate + ".");
        }
    }
}
