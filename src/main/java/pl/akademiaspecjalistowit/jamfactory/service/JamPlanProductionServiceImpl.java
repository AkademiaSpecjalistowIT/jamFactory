package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.model.*;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;
    private final ApiProperties apiProperties;
    private final JarService jarService;

    @Override
    @Transactional
    public void addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity =
                jamsMapper.toEntity(jamPlanProductionRequestDto, apiProperties.getMaxProductionLimit());

        validateProductionPlan(jamPlanProductionRequestDto);

        try {
            addNewProductionPlanForGivenDay(entity);
        } catch (ProductionException e) {
            log.info("Procesujemy dodanie planu prodyjnego na więcej niż 1 dzien, ponieważ : " + e.getMessage());
            addProductionPlanBeforeDeadline(entity);
        }
        createJarOrder(jamPlanProductionRequestDto);
    }

    @Override
    public JamListPlanProductionResponseDto getPlanProduction() {
        LocalDate today = LocalDate.now();
        LocalDate todayPlusSevenDays = LocalDate.now().plusDays(7);

        List<JamPlanProductionResponseDto> list = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .map(entity -> jamsMapper.toResponse(entity))
                .toList();

        int sumSmallJamJars = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .mapToInt(plan -> plan.getSmallJamJars())
                .sum();

        int sumMediumJamJars = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .mapToInt(plan -> plan.getMediumJamJars())
                .sum();

        int sumLargeJamJars = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .mapToInt(plan -> plan.getLargeJamJars())
                .sum();

        return new JamListPlanProductionResponseDto(list, sumSmallJamJars, sumMediumJamJars, sumLargeJamJars);
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

    private void createJarOrder(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JarOrderRequestDto jarOrderRequestDto = new JarOrderRequestDto(jamPlanProductionRequestDto.getPlanDate().plusDays(1),
                jamPlanProductionRequestDto.getSmallJamJars(), jamPlanProductionRequestDto.getMediumJamJars(),
                jamPlanProductionRequestDto.getLargeJamJars());

        UUID jarOrderRequestId = jarService.orderJars(jarOrderRequestDto);
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
            throw new ProductionException(
                    "Przekraczajaca zdolnośc transportowa na period z " + today + " po " + checkUntilDate + ".");
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