package pl.akademiaspecjalistowit.jamfactory.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.akademiaspecjalistowit.jamfactory.dto.*;
import pl.akademiaspecjalistowit.jamfactory.service.JamPlanProductionService;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/jams")
public class JamFactoryController {
    private final JamPlanProductionService jamPlanProductionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/product-plan")
    public UUID addProductionPlan(@RequestBody JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        return jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product-plan")
    public ResponseEntity<String> findProductionPlan(@RequestBody JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("NOT_IMPLEMENTED");
    }
}
