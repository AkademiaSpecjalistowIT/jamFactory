package pl.akademiaspecjalistowit.jamfactory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JamJars {
    private Integer smallJamJars;
    private Integer mediumJamJars;
    private Integer largeJamJars;

    public Integer borrowMedium(Integer amount) {
        if (amount > this.mediumJamJars) {
            return this.mediumJamJars;
        }
        return mediumJamJars - amount;
    }

    public Integer borrowSmall(Integer amount) {
        if (amount > this.smallJamJars) {
            return this.smallJamJars;
        }
        return smallJamJars - amount;
    }

    public Integer borrowLarge(Integer amount) {
        if (amount > this.largeJamJars) {
            return largeJamJars;
        }
        return largeJamJars - amount;
    }

    public boolean isEmpty() {
        return smallJamJars + mediumJamJars + largeJamJars == 0;
    }
}
