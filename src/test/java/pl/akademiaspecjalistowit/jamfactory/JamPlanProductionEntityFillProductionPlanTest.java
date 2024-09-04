package pl.akademiaspecjalistowit.jamfactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import pl.akademiaspecjalistowit.jamfactory.dto.JamJars;

class JamPlanProductionEntityFillProductionPlanTest {

    @Test
    void should_fill_JamPlanProductionEntity() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(1000, 1000, 1000);

        //when
        JamPlanProductionEntity jamPlanProductionEntityFilled = jamPlanProductionEntity.fillProductionPlan(jamJars);

        //then
        assertThat(jamPlanProductionEntityFilled.getTotalJamWeight()).isEqualTo(1750);
    }

    @Test
    void should_fill_JamPlanProductionEntity_without_exceeding_limit() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(1000, 1000, 10000);

        //when
        JamPlanProductionEntity jamPlanProductionEntityFilled = jamPlanProductionEntity.fillProductionPlan(jamJars);

        //then
        assertThat(jamPlanProductionEntityFilled.getTotalJamWeight()).isCloseTo(2000d, Offset.offset(0.24));
    }

}