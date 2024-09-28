package pl.akademiaspecjalistowit.jamfactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import pl.akademiaspecjalistowit.jamfactory.model.JamJars;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;

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

    @Test
    void should_fill_JamPlanProductionEntity_firstly_large_jars() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(1000, 1000, 2000);

        //when
        JamPlanProductionEntity jamPlanProductionEntityFilled = jamPlanProductionEntity.fillProductionPlan(jamJars);

        //then
        assertThat(jamPlanProductionEntityFilled.getTotalJamWeight()).isEqualTo(2000);
        assertThat(jamPlanProductionEntityFilled.getLargeJamJars()).isEqualTo(2000);
        assertThat(jamPlanProductionEntityFilled.getMediumJamJars()).isEqualTo(0);
        assertThat(jamPlanProductionEntityFilled.getSmallJamJars()).isEqualTo(0);
    }

    @Test
    void should_fill_JamPlanProductionEntity_firstly_large_jars_secondly_medium_jars() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(1000, 2000, 1500);

        //when
        JamPlanProductionEntity jamPlanProductionEntityFilled = jamPlanProductionEntity.fillProductionPlan(jamJars);

        //then
        assertThat(jamPlanProductionEntityFilled.getTotalJamWeight()).isEqualTo(2000);
        assertThat(jamPlanProductionEntityFilled.getLargeJamJars()).isEqualTo(1500);
        assertThat(jamPlanProductionEntityFilled.getMediumJamJars()).isEqualTo(1000);
        assertThat(jamPlanProductionEntityFilled.getSmallJamJars()).isEqualTo(0);
    }

    @Test
    public void test_production_within_limit() {
        // GIVEN
        JamPlanProductionEntity productionPlan = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(500, 1000, 1000);

        // WHEN
        JamPlanProductionEntity jamPlanProductionEntity = productionPlan.fillProductionPlan(jamJars);

        // THEN
        assertThat(jamPlanProductionEntity.getTotalJamWeight()).isLessThan(2000);
    }

    @Test
    public void test_production_exceeds_limit() {
        // GIVEN
        JamPlanProductionEntity productionPlan = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(5000, 5000, 5000);

        // WHEN
        JamPlanProductionEntity jamPlanProductionEntity = productionPlan.fillProductionPlan(jamJars);

        // THEN
        assertThat(jamPlanProductionEntity.getTotalJamWeight()).isEqualTo(2000);
    }
}