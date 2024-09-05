package pl.akademiaspecjalistowit.jamfactory.service;

import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JamListPlanProductionResponseDto;

import java.util.List;
import java.util.UUID;

public interface JamPlanProductionService {
    UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto);

    JamListPlanProductionResponseDto getPlanProduction();
}
