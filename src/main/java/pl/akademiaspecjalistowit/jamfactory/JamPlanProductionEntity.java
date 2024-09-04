package pl.akademiaspecjalistowit.jamfactory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;
import pl.akademiaspecjalistowit.jamfactory.dto.JamJars;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "production_table")
public class JamPlanProductionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private UUID planId;

    @Column(nullable = false, unique = true)
    private LocalDate planDate;

    @Column(nullable = false)
    private Integer smallJamJars;

    @Column(nullable = false)
    private Integer mediumJamJars;

    @Column(nullable = false)
    private Integer largeJamJars;

    private Integer productionLimitInKg;

    public JamPlanProductionEntity(LocalDate planDate, Integer smallJamJars, Integer mediumJamJars, Integer largeJamJars, Integer productionLimitInKg) {
        this.smallJamJars = smallJamJars == null ? 0:smallJamJars;
        this.largeJamJars = largeJamJars == null ? 0:largeJamJars;
        this.mediumJamJars = mediumJamJars == null ? 0:mediumJamJars;
        this.planId = UUID.randomUUID();
        this.planDate = planDate;
        this.productionLimitInKg = productionLimitInKg;
    }

    public JamPlanProductionEntity(LocalDate planDate, Integer productionLimitInKg){
        this(planDate,null,null,null, productionLimitInKg);
    }


    public double getTotalJamWeight(){
        return (JarSizes.LARGE.value * largeJamJars) + (JarSizes.MEDIUM.value * mediumJamJars) + (JarSizes.SMALL.value * smallJamJars);
    }
    public static double getLargeWeight() {
        return JarSizes.LARGE.value;
    }

    public static double getMediumWeight() {
        return JarSizes.MEDIUM.value;
    }

    public static double getSmallWeight() {
        return JarSizes.SMALL.value;
    }

    public void setSmallJamJars(Integer smallJamJars) {
        this.smallJamJars = smallJamJars;
    }

    public void setMediumJamJars(Integer mediumJamJars) {
        this.mediumJamJars = mediumJamJars;
    }

    public void setLargeJamJars(Integer largeJamJars) {
        this.largeJamJars = largeJamJars;
    }

    public void updatePlan(JamPlanProductionEntity newProductionPlan) {
        this.largeJamJars += newProductionPlan.getLargeJamJars();
        this.smallJamJars += newProductionPlan.getSmallJamJars();
        this.mediumJamJars += newProductionPlan.getMediumJamJars();
    }

    public JamPlanProductionEntity fillProductionPlan(JamJars jars) {
        int i = calculateJamJarsAmountToFit(JarSizes.LARGE);
        Integer actuallyBorrowedLargeAmount = jars.borrowLarge(i);
        largeJamJars += actuallyBorrowedLargeAmount;

        int ii = calculateJamJarsAmountToFit(JarSizes.MEDIUM);
        Integer actuallyBorrowedMediumAmount = jars.borrowMedium(ii);
        mediumJamJars += actuallyBorrowedMediumAmount;

        int iii = calculateJamJarsAmountToFit(JarSizes.SMALL);
        Integer actuallyBorrowedSmallAmount = jars.borrowSmall(iii);
        smallJamJars += actuallyBorrowedSmallAmount;
        return this;
    }

    private int calculateJamJarsAmountToFit(JarSizes jarSizes) {
        double kgToFitInProductionPlan = productionLimitInKg - getTotalJamWeight();
        return (int)(kgToFitInProductionPlan / jarSizes.value);
    }

    private enum JarSizes{

        LARGE(1),
        MEDIUM(0.5),
        SMALL(0.25);
        private final double value;

        JarSizes(double value) {
            this.value = value;
        }
    }
}
