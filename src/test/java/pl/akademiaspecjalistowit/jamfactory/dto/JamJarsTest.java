package pl.akademiaspecjalistowit.jamfactory.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;

class JamJarsTest {

    @Test
    void should_empty_Large_jamJar() {
        //given
        JamJars jamJars = new JamJars(0, 0, 1000);

        //when
        jamJars.borrowLarge(1000);

        //then
        assertThat(jamJars.isEmpty()).isTrue();
    }


    @Test
    void should_empty_small_jamJar() {
        //given
        JamJars jamJars = new JamJars(1000, 0, 0);

        //when
        jamJars.borrowSmall(1000);

        //then
        assertThat(jamJars.isEmpty()).isTrue();
    }


    @Test
    void should_empty_medium_jamJar() {
        //given
        JamJars jamJars = new JamJars(0, 1000, 0);

        //when
        jamJars.borrowMedium(1000);

        //then
        assertThat(jamJars.isEmpty()).isTrue();
    }

    @Test
    void should_empty_medium_jamJar_with_higher_than_initial_value() {
        //given
        JamJars jamJars = new JamJars(0, 1000, 0);

        //when
        jamJars.borrowMedium(1200);

        //then
        assertThat(jamJars.isEmpty()).isTrue();
    }

}