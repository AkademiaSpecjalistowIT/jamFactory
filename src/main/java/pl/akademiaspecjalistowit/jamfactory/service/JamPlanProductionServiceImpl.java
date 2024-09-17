package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.JamPlanProductionServiceException;
import pl.akademiaspecjalistowit.jamfactory.exception.JarFactoryHttpClientException;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.model.JamListPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.model.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;
    private final ApiProperties apiProperties;
    private final JarService jarService;
    private final ProductionPlanForGivenDayService productionPlanForGivenDayService;
    private final ProductionPlanBeforeDeadline productionPlanBeforeDeadline;

    @Override
    @Transactional
    public void addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity =
                jamsMapper.toEntity(jamPlanProductionRequestDto, apiProperties.getMaxProductionLimit());

        validateProductionPlan(jamPlanProductionRequestDto);

        try {
            productionPlanForGivenDayService.addNewProductionPlanForGivenDay(entity);
        } catch (ProductionException e) {
            log.info("Procesujemy dodanie planu prodyjnego na więcej niż 1 dzien, ponieważ : " + e.getMessage());
            productionPlanBeforeDeadline.addProductionPlanBeforeDeadline(entity);
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

    private void createJarOrder(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JarOrderRequestDto jarOrderRequestDto = new JarOrderRequestDto(jamPlanProductionRequestDto.getPlanDate().plusDays(1),
                jamPlanProductionRequestDto.getSmallJamJars(), jamPlanProductionRequestDto.getMediumJamJars(),
                jamPlanProductionRequestDto.getLargeJamJars());

        try {
            jarService.orderJars(jarOrderRequestDto);
        } catch (JarFactoryHttpClientException e) {
            throw new JamPlanProductionServiceException("Failed to create jar order", e);
        }
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
}
