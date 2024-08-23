package pl.akademiaspecjalistowit.jamfactory.service;

import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;

import java.util.UUID;

public interface JamPlanProductionService {
    UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto);
}
