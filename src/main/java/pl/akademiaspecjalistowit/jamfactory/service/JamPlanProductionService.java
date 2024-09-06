package pl.akademiaspecjalistowit.jamfactory.service;

import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JamPlanProductionService {

    UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto);
}
