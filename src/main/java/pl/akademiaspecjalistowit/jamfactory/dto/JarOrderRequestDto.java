package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class JarOrderRequestDto {
    private LocalDate deliveryDate;
    private Integer smallJars;
    private Integer mediumJars;
    private Integer largeJars;
}
