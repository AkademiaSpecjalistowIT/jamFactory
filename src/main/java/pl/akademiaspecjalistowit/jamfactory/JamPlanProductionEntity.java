package pl.akademiaspecjalistowit.jamfactory;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

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

    @Column(nullable = false)
    private LocalDate planDate;

    @Column(nullable = false)
    private Integer smallJamJars;

    @Column(nullable = false)
    private Integer mediumJamJars;

    @Column(nullable = false)
    private Integer largeJamJars;

    public JamPlanProductionEntity(LocalDate planDate, Integer smallJamJars, Integer mediumJamJars, Integer largeJamJars) {
        this.smallJamJars = smallJamJars == null ? 0:smallJamJars;
        this.largeJamJars = largeJamJars == null ? 0:largeJamJars;
        this.mediumJamJars = mediumJamJars == null ? 0:mediumJamJars;
        this.planId = UUID.randomUUID();
        this.planDate = planDate;
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

    private enum JarSizes{

        LARGE(1),
        //MEDIUM(0.5),
        MEDIUM(1),
        //SMALL(0.25);
        SMALL(1);
        private final double value;

        JarSizes(double value) {
            this.value = value;
        }
    }
}
