package pl.akademiaspecjalistowit.jamfactory.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.akademiaspecjalistowit.jamfactory.dto.*;
import pl.akademiaspecjalistowit.jamfactory.service.JamPlanProductionService;

import java.time.LocalDate;
import java.util.List;
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
    public ProductionPlanForecastDto findProductionPlan(@RequestBody JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        return new ProductionPlanForecastDto(
                List.of(
                    new JamPlanProductionResponseDto(100,100,100, LocalDate.of(2024,8,29)),
                    new JamPlanProductionResponseDto(100,100,100, LocalDate.of(2024,8,30)),
                    new JamPlanProductionResponseDto(100,100,100, LocalDate.of(2024,8,31))),
                new JamQuantityDto(300,300,300));
    }
}
