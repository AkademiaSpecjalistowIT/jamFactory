package pl.akademiaspecjalistowit.jamfactory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.akademiaspecjalistowit.jamfactory.exception.JamJarsException;

@Getter
public class JamJars {
    private Integer smallJamJars;
    private Integer mediumJamJars;
    private Integer largeJamJars;

    public JamJars(Integer smallJamJars, Integer mediumJamJars, Integer largeJamJars) {
        validateInputFields(smallJamJars, mediumJamJars, largeJamJars);
        this.smallJamJars = smallJamJars;
        this.mediumJamJars = mediumJamJars;
        this.largeJamJars = largeJamJars;
    }

    private void validateInputFields(Integer smallJamJars, Integer mediumJamJars, Integer largeJamJars) {
        if (smallJamJars < 0 || mediumJamJars < 0 || largeJamJars < 0) {
            throw new JamJarsException("Wartości nie mogą być niegatywne!!!");
        }
    }

    public Integer borrowMedium(Integer amount) {
        if (amount < 0) {
            throw new JamJarsException("Wartości nie mogą być niegatywne!!!");
        }
        if (amount >= this.mediumJamJars) {
            return mediumJamJars;
        }
        return amount;
    }

    public Integer borrowSmall(Integer amount) {
        if(amount<0){
            throw new JamJarsException("Wartości nie mogą być niegatywne!!!");
        }
        if (amount >= this.smallJamJars) {
            return this.smallJamJars;
        }
        return amount;
    }

    public Integer borrowLarge(Integer amount) {
        if(amount<0){
            throw new JamJarsException("Wartości nie mogą być niegatywne!!!");
        }
        if (amount >= this.largeJamJars) {
            return this.largeJamJars;
        }
        return amount;
    }

    public boolean isEmpty() {
        return smallJamJars + mediumJamJars + largeJamJars == 0;
    }

    public void updateLarge(int actuallyBorrowedLargeAmount) {
        largeJamJars -= actuallyBorrowedLargeAmount;
    }

    public void updateMedium(int actuallyBorrowedLargeAmount) {
        mediumJamJars -= actuallyBorrowedLargeAmount;
    }

    public void updateSmall(int actuallyBorrowedLargeAmount) {
        smallJamJars -= actuallyBorrowedLargeAmount;
    }
}
