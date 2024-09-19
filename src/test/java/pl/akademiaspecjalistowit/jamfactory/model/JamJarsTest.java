package pl.akademiaspecjalistowit.jamfactory.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.akademiaspecjalistowit.jamfactory.exception.JamJarsException;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JamJarsTest {

    @Test
    void should_borrow_all_large_jars_when_requested_amount_equals_current_stock() {
        //given
        JamJars jamJars = new JamJars(0, 0, 1000);

        //when
        Integer borrowedLarge = jamJars.borrowLarge(1000);

        //then
        //assertThat(jamJars.isEmpty()).isTrue();
        assertThat(borrowedLarge).isEqualTo(1000);
    }


    @Test
    void should_borrow_all_small_jars_when_requested_amount_equals_current_stock() {
        //given
        JamJars jamJars = new JamJars(1000, 0, 0);

        //when
        Integer borrowedSmall = jamJars.borrowSmall(1000);

        //then
        //assertThat(jamJars.isEmpty()).isTrue();
        assertThat(borrowedSmall).isEqualTo(1000);
    }


    @Test
    void should_borrow_all_medium_jars_when_requested_amount_equals_current_stock() {
        //given
        JamJars jamJars = new JamJars(0, 1000, 0);

        //when
        Integer borrowedMedium = jamJars.borrowMedium(1000);

        //then
        //assertThat(jamJars.isEmpty()).isTrue();
        assertThat(borrowedMedium).isEqualTo(1000);
    }

    @Test
    void should_borrow_all_medium_jars_with_higher_than_initial_value() {
        //given
        JamJars jamJars = new JamJars(0, 1000, 0);

        //when
        Integer borrowedMedium = jamJars.borrowMedium(1200);

        //then
        //assertThat(jamJars.isEmpty()).isTrue();
        assertThat(borrowedMedium).isEqualTo(1000);
    }

    @Test
    void should_borrow_medium_jars_equals_requested_quantity_when_request_less_than_initial_value() {
        //given
        JamJars jamJars = new JamJars(0, 1000, 0);

        //when
        Integer borrowedMedium = jamJars.borrowMedium(500);

        //then
        assertThat(borrowedMedium).isEqualTo(500);
    }

    @Test
    void should_throw_exception_when_small_jars_less_than_zero() {
        //given&when
        Executable e = () -> new JamJars(-20, 0, 0);

        //then
        assertThrows(JamJarsException.class, e);
    }
    @Test
    void should_throw_exception_when_medium_jars_less_than_zero() {
        //given&when
        Executable e = () -> new JamJars(0, -10, 0);

        //then
        assertThrows(JamJarsException.class, e);
    }

    @Test
    void should_throw_exception_when_large_jars_less_than_zero() {
        //given&when
        Executable e = () -> new JamJars(0, 0, -5);

        //then
        assertThrows(JamJarsException.class, e);
    }

}