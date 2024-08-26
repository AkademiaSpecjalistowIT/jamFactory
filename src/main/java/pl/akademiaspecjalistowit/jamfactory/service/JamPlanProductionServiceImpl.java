package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Override
    public UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity = jamsMapper.toEntity(jamPlanProductionRequestDto);
        validateProductionPlan(jamPlanProductionRequestDto);
        jamPlanProductionRepository.save(entity);
        return entity.getPlanId();
    }

//    Dodanie nowego Planu produkcyjnego jest możliwe jeżeli:
//    w ramach jednego dnia ilość dostarczonych słoików (wynikająca z istnejącego dziennego planu produkcyjnych) nie można być większa niż X.
//            (Ilość dni x maxDeliveryCapacity - aktualne plany produkcyjne) >= nowy plan
//    X jest elementem parametryzowanym w application.yaml
//    MaxDeliveryCapacity: 15 000

    private void validateProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        LocalDate planDate = jamPlanProductionRequestDto.getPlanDate();
        Integer newPlanTotalJars = jamPlanProductionRequestDto.getSmallJamJars() + jamPlanProductionRequestDto.getMediumJamJars() + jamPlanProductionRequestDto.getLargeJamJars();

        // Получаем текущие производственные планы на указанный день
        List<JamPlanProductionEntity> existingPlans = jamPlanProductionRepository.findAll();
        Integer existingJarsForTheDay = existingPlans.stream()
                .filter(plan -> plan.getPlanDate().equals(planDate))
                .mapToInt(plan -> plan.getSmallJamJars() + plan.getMediumJamJars() + plan.getLargeJamJars())
                .sum();

        // Проверка на превышение максимальной вместимости
        if (existingJarsForTheDay + newPlanTotalJars > apiProperties.getMaxDeliveryCapacity()) {
            throw new ProductionException("Превышен лимит на количество банок для данного дня");
        }
    }
}
