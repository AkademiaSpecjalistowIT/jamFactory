package pl.akademiaspecjalistowit.jamfactory;

import org.junit.jupiter.api.Test;
import pl.akademiaspecjalistowit.jamfactory.controller.JamController;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class JamPlanProductionEntityTest {
    private JamController jamPlanProductionServiceImpl;

    @Test
    void ShouldConvertAllJamJarsToWeightCorrectly() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 10, 10, 10,2000);

        //when
        double totalJamWeight = jamPlanProductionEntity.getTotalJamWeight();

        //then
        assertThat(totalJamWeight).isEqualTo(17.5);
    }

    @Test
    void ShouldConvertAllJamJarsToWeightIgnoreMissingSmallJamJars() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), null, 10, 10,2000);

        //when
        double totalJamWeight = jamPlanProductionEntity.getTotalJamWeight();

        //then
        assertThat(totalJamWeight).isEqualTo(15);
    }

    @Test
    void ShouldConvertAllJamJarsToWeightIgnoreMissingMeidumJamJars() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 10, null, 10, 2000);

        //when
        double totalJamWeight = jamPlanProductionEntity.getTotalJamWeight();

        //then
        assertThat(totalJamWeight).isEqualTo(12.5);
    }

    @Test
    void ShouldConvertAllJamJarsToWeightIgnoreMissingLargeJamJars() {
        //given
        JamPlanProductionEntity jamPlanProductionEntity = new JamPlanProductionEntity(LocalDate.now(), 10, 10, null, 2000);

        //when
        double totalJamWeight = jamPlanProductionEntity.getTotalJamWeight();

        //then
        assertThat(totalJamWeight).isEqualTo(7.5);
    }

}