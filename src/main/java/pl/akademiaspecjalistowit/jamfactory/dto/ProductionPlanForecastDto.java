package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductionPlanForecastDto {
    private List<JamPlanProductionResponseDto> perDay;
    private JamQuantityDto sum;
}
