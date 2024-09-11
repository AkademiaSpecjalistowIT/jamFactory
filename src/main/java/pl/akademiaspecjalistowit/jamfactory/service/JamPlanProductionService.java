package pl.akademiaspecjalistowit.jamfactory.service;

import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamListPlanProductionResponseDto;

import java.util.UUID;

public interface JamPlanProductionService {

//    UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto);
    void addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto);

    JamListPlanProductionResponseDto getPlanProduction();
}
