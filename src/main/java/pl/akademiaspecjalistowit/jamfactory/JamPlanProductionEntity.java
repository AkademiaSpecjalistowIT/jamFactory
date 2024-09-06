//package pl.akademiaspecjalistowit.jamfactory;
//
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
//@Entity
//@Table(name = "production_table")
//public class JamPlanProductionEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private long id;
//
//    @Column(nullable = false)
//    private UUID planId;
//
//    @Column(nullable = false)
//    private LocalDate planDate;
//
//    @Column(nullable = false)
//    private Integer smallJamJars;
//
//    @Column(nullable = false)
//    private Integer mediumJamJars;
//
//    @Column(nullable = false)
//    private Integer largeJamJars;
//
//    public JamPlanProductionEntity(LocalDate planDate, Integer smallJamJars, Integer mediumJamJars, Integer largeJamJars) {
//        this.planId = UUID.randomUUID();
//        this.planDate = planDate;
//        this.smallJamJars = smallJamJars;
//        this.mediumJamJars = mediumJamJars;
//        this.largeJamJars = largeJamJars;
//    }
//}
