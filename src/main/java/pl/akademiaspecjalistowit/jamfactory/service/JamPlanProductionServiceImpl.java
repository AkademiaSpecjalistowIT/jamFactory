package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;
    private final ApiProperties apiProperties;

    @Override
    public UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity = jamsMapper.toEntity(jamPlanProductionRequestDto);
        validateProductionPlan(jamPlanProductionRequestDto);
        jamPlanProductionRepository.save(entity);
        return entity.getPlanId();
    }

//    Dodanie nowego Planu produkcyjnego jest możliwe jeżeli:

//    w ramach jednego dnia ilość dostarczonych słoików (wynikająca z istnejącego dziennego planu produkcyjnych)
//    nie można być większa niż X.

//    (Ilość dni x maxDeliveryCapacity - aktualne plany produkcyjne) >= nowy plan

//    X jest elementem parametryzowanym w application.yaml

//    MaxDeliveryCapacity: 15 000

    private void validateProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        LocalDate today = LocalDate.now();
        LocalDate plannedPlanDate = jamPlanProductionRequestDto.getPlanDate();
        Integer maxDeliveryCapacityPerDay = apiProperties.getMaxDeliveryCapacity();

        Integer totalJamJarsForPlannedDay = jamPlanProductionRequestDto.getSmallJamJars() +
                jamPlanProductionRequestDto.getMediumJamJars() +
                jamPlanProductionRequestDto.getLargeJamJars();

        if (totalJamJarsForPlannedDay > maxDeliveryCapacityPerDay) {

            List<JamPlanProductionEntity> planProductionBeforePlannedPlanDate = jamPlanProductionRepository.findAll().stream()
                    .filter(plan -> plan.getPlanDate().isBefore(plannedPlanDate))
                    .toList();

            Integer existingSumJarsBeforePlanDay = jamPlanProductionRepository.findAll().stream()
                    .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(plannedPlanDate))
                    .mapToInt(plan -> plan.getSmallJamJars() + plan.getMediumJamJars() + plan.getLargeJamJars())
                    .sum();

            Long daysInRange = planProductionBeforePlannedPlanDate.stream()
                    .filter(plan -> plan.getPlanDate().isBefore(today))
                    .count();

            long maxCapacityInRange = daysInRange * maxDeliveryCapacityPerDay;

            long availableSpace = maxCapacityInRange - existingSumJarsBeforePlanDay;

            if (totalJamJarsForPlannedDay > availableSpace) {
                throw new ProductionException("Przekroczono zdolność transportową. Dostępne miejsce tylko na " + availableSpace + " słoików.");
            }
            throw new ProductionException("Przekroczono dzienną zdolność transportową. Spróbuj rozdzielić na wcześniejsze dniю");
        }
    }

    private void validateProductionPlan2(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        LocalDate today = LocalDate.now();

        LocalDate plannedPlanDate = jamPlanProductionRequestDto.getPlanDate();

        Integer maxDeliveryCapacityPerDay = apiProperties.getMaxDeliveryCapacity();

        Integer totalJamJarsForPlannedDay = jamPlanProductionRequestDto.getSmallJamJars() +
                jamPlanProductionRequestDto.getMediumJamJars() +
                jamPlanProductionRequestDto.getLargeJamJars();

        Integer moreThanMaxDeliveryCapacityPerDay = totalJamJarsForPlannedDay - maxDeliveryCapacityPerDay;

        List<JamPlanProductionEntity> planProductionBeforePlannedPlanDate = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> plan.getPlanDate().isBefore(plannedPlanDate))
                .toList();

        Integer existingSumJarsBeforePlanDay = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(plannedPlanDate))
                .mapToInt(plan -> plan.getSmallJamJars() + plan.getMediumJamJars() + plan.getLargeJamJars())
                .sum();

        Long countDaysBeforePlanDay = planProductionBeforePlannedPlanDate.stream()
                .filter(plan -> plan.getPlanDate().isBefore(today))
                .count();

        Long maxDeliveryCapacityBeforePlanDay = countDaysBeforePlanDay * maxDeliveryCapacityPerDay;

        long howManyPlaceWeHave = maxDeliveryCapacityBeforePlanDay - existingSumJarsBeforePlanDay;

        long more = howManyPlaceWeHave - moreThanMaxDeliveryCapacityPerDay;

        if (totalJamJarsForPlannedDay > maxDeliveryCapacityPerDay) {
            if (moreThanMaxDeliveryCapacityPerDay > howManyPlaceWeHave) {
                throw new ProductionException("przekraczajaca zdolnośc transportowa. Mamy mejsce tylko na " + more);
            }
            throw new ProductionException("przekraczajaca zdolnośc transportowa. Mozna przelogyc na poprzedni dni");
        }
    }
}
