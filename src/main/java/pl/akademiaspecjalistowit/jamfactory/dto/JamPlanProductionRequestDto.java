package pl.akademiaspecjalistowit.jamfactory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class JamPlanProductionRequestDto {
    @NotNull(message = "Data planu nie może być pusta")
    private LocalDate planDate;

    @NotNull(message = "Ilość słoików nie może być pusta")
    @Min(value = 0, message = "Ilość słoików nie może być negatywna")
    private Integer smallJamJars;

    @NotNull(message = "Ilość słoików nie może być pusta")
    @Min(value = 0, message = "Ilość słoików nie może być negatywna")
    private Integer mediumJamJars;

    @NotNull(message = "Ilość słoików nie może być pusta")
    @Min(value = 0, message = "Ilość słoików nie może być negatywna")
    private Integer largeJamJars;
}
