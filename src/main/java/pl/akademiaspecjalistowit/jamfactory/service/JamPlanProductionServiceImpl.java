package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

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
        validateAddingNewProductionPlanPossibility(entity);
        jamPlanProductionRepository.save(entity);
        return entity.getPlanId();
    }

    private void validateAddingNewProductionPlanPossibility(JamPlanProductionEntity entity) {
        List<JamPlanProductionEntity> allByDate = jamPlanProductionRepository.findAllByPlanDate(entity.getPlanDate());

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
            System.out.println("Exceeded limit by: " + exceededLimit + " Kg");
            throw new ProductionException("Przekroczono limit produkcyjny o " + exceededLimit + " Kg");
        }
    }
}