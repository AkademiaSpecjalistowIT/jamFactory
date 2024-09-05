package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JamListPlanProductionResponseDto {
    private List<JamPlanProductionResponseDto> listPlans;
    private Integer sumSmallJamJars;
    private Integer sumMediumJamJars;
    private Integer sumLargeJamJars;
}
