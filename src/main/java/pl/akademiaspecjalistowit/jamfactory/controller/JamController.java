package pl.akademiaspecjalistowit.jamfactory.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.akademiaspecjalistowit.jamfactory.model.JamListPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.model.OrderResponse;
import pl.akademiaspecjalistowit.jamfactory.service.JamPlanProductionService;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1")
public class JamController implements JamsApi {
    private final JamPlanProductionService jamPlanProductionService;

    @Override
    public ResponseEntity<OrderResponse> createJamProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        UUID orderId = jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
        OrderResponse response = new OrderResponse(orderId.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @Override
//    public ResponseEntity<Void> createJamProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
//        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }

    @Override
    public ResponseEntity<JamListPlanProductionResponseDto> getProductionPlan() {
        JamListPlanProductionResponseDto planProduction = jamPlanProductionService.getPlanProduction();
        return ResponseEntity.status(HttpStatus.OK).body(planProduction);
    }
}
