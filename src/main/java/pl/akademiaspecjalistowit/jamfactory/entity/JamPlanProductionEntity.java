package pl.akademiaspecjalistowit.jamfactory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

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

    @Min(value = 0, message = "Ilość słoików nie może być negatywna")
    @Column(nullable = false)
    private Integer smallJamJars;

    @Min(value = 0, message = "Ilość słoików nie może być negatywna")
    @Column(nullable = false)
    private Integer mediumJamJars;

    @Min(value = 0, message = "Ilość słoików nie może być negatywna")
    @Column(nullable = false)
    private Integer largeJamJars;

    public JamPlanProductionEntity(LocalDate planDate, Integer smallJamJars, Integer mediumJamJars, Integer largeJamJars) {
        this.planId = UUID.randomUUID();
        this.planDate = planDate;
        this.smallJamJars = smallJamJars;
        this.mediumJamJars = mediumJamJars;
        this.largeJamJars = largeJamJars;
    }
}
