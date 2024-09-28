package pl.akademiaspecjalistowit.jamfactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.model.JamJars;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

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
    void should_fill_JamPlanProductionEntity_firstly_large_jars_secondly_medium_jars_and_thirdly_small_jars() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 2000);
        JamJars jamJars = new JamJars(7000, 500, 500);

        //when
        JamPlanProductionEntity jamPlanProductionEntityFilled = jamPlanProductionEntity.fillProductionPlan(jamJars);

        //then
        assertThat(jamPlanProductionEntityFilled.getTotalJamWeight()).isEqualTo(2000);
        assertThat(jamPlanProductionEntityFilled.getLargeJamJars()).isEqualTo(500);
        assertThat(jamPlanProductionEntityFilled.getMediumJamJars()).isEqualTo(500);
        assertThat(jamPlanProductionEntityFilled.getSmallJamJars()).isEqualTo(5000);
    }
}