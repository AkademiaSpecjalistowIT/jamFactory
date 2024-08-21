package pl.akademiaspecjalistowit.jamfactory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProduktionRequestDto;

@RestController
@RequestMapping("/api/v1/jams")
public class JamFactoryController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/product-plan")
    public void addProductionPlan(@RequestBody JamPlanProduktionRequestDto jamPlanProduktionRequestDto) {
        ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED);
        }
}
