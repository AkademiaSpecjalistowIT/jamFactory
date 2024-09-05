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
        if (amount >= this.mediumJamJars) {
            return mediumJamJars;
        }
        return amount;
    }

    public Integer borrowSmall(Integer amount) {
        if (amount > this.smallJamJars) {
            return this.smallJamJars;
        }
        return amount;
    }

    public Integer borrowLarge(Integer amount) {
        if (amount > this.largeJamJars) {
//            Integer tempLarge = this.largeJamJars;
//            updateLarge(largeJamJars);
            return largeJamJars;
        }
//        Integer tempLarge = largeJamJars - amount;
//        updateLarge(largeJamJars);
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
