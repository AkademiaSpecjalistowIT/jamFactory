package pl.akademiaspecjalistowit.jamfactory.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

}