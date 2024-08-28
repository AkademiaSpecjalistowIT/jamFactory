package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JamPlanProductionResponseDto extends JamQuantityDto {
    private LocalDate productionDate;

    public JamPlanProductionResponseDto(Integer smallJar, Integer mediumJar, Integer largeJar, LocalDate productionDate) {
        super(smallJar, mediumJar, largeJar);
        this.productionDate = productionDate;
    }
}
