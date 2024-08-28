package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JamQuantityDto {
    protected Integer smallJar;
    protected Integer mediumJar;
    protected Integer largeJar;
}
