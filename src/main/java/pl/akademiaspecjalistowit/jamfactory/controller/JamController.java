package pl.akademiaspecjalistowit.jamfactory.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.akademiaspecjalistowit.jamfactory.model.JamListPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.service.JamPlanProductionService;

@AllArgsConstructor
@RestController
public class JamController implements ApiApi {
    private final JamPlanProductionService jamPlanProductionService;

    @Override
    public ResponseEntity<Void> createJamProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<JamListPlanProductionResponseDto> getProductionPlan() {
        JamListPlanProductionResponseDto planProduction = jamPlanProductionService.getPlanProduction();
        return ResponseEntity.status(HttpStatus.OK).body(planProduction);
    }
}
