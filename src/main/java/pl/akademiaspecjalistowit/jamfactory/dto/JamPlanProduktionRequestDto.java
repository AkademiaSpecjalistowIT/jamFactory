package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class JamPlanProduktionRequestDto {
    private LocalDate planDate;
    private Integer smallJamJars;
    private Integer mediumJamJars;
    private Integer largeJamJars;
}
