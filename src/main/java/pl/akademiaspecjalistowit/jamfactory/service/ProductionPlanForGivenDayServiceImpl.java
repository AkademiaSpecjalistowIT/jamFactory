package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.configuration.ApiProperties;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;

@AllArgsConstructor
@Service
public class ProductionPlanForGivenDayServiceImpl implements ProductionPlanForGivenDayService{
    private final ApiProperties apiProperties;
    private final JamsMapper jamsMapper;
    private final JamPlanProductionRepository jamPlanProductionRepository;

    @Override
    public void addNewProductionPlanForGivenDay(JamPlanProductionEntity newProductionPlan) {
        double maxProductionLimit = apiProperties.getMaxProductionLimit();
        double totalJamWeight = newProductionPlan.getTotalJamWeight();

        validateFittingNewProductionPlanInEmptyDay(maxProductionLimit, totalJamWeight);

        LocalDate newPlanDate = newProductionPlan.getPlanDate();
        JamPlanProductionEntity jamPlanProductionEntity = jamPlanProductionRepository.findByPlanDate(newPlanDate)
                .map(e -> updateExistingPlan(newProductionPlan, maxProductionLimit, totalJamWeight, e))
                .orElse(newProductionPlan);
        jamPlanProductionRepository.save(jamPlanProductionEntity);
    }

    private JamPlanProductionEntity updateExistingPlan(JamPlanProductionEntity newProductionPlan, double maxProductionLimit,
                                                       double totalJamWeight, JamPlanProductionEntity e) {
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
}
