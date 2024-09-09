package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class JamPlanProductionResponseDto {
    private LocalDate planDate;
    private Integer smallJamJars;
    private Integer mediumJamJars;
    private Integer largeJamJars;
}
