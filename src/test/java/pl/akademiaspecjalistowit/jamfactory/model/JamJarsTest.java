package pl.akademiaspecjalistowit.jamfactory.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import pl.akademiaspecjalistowit.jamfactory.exception.JamJarsException;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void should_throw_exceptions_when_borrowLarge_have_negative_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowLarge(-1);

        //THEN
        assertThrows(JamJarsException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowLarge_is_null() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowLarge(null);

        //THEN
        assertThrows(NullPointerException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowLarge_have_larger_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowMedium(null);

        //THEN
        assertThrows(NullPointerException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowLarge_have_smaller_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowLarge = jamJars.borrowLarge(9);

        //THEN
        assertThat(borrowLarge).isEqualTo(9);
    }

    @Test
    public void should_throw_exceptions_when_borrowLarge_have_equals_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowLarge = jamJars.borrowLarge(10);

        //THEN
        assertThat(borrowLarge).isEqualTo(10);
    }

    @Test
    public void should_throw_exceptions_when_borrowSmall_have_negative_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowSmall(-1);

        //THEN
        assertThrows(JamJarsException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowSmall_is_null() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowSmall(null);

        //THEN
        assertThrows(NullPointerException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowSmall_have_larger_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowSmall = jamJars.borrowSmall(11);

        //THEN
        assertThat(borrowSmall).isEqualTo(10);
    }

    @Test
    public void should_throw_exceptions_when_borrowSmall_have_smaller_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowSmall = jamJars.borrowSmall(9);

        //THEN
        assertThat(borrowSmall).isEqualTo(9);
    }

    @Test
    public void should_throw_exceptions_when_borrowSmall_have_equals_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowSmall = jamJars.borrowSmall(10);

        //THEN
        assertThat(borrowSmall).isEqualTo(10);
    }

    @Test
    public void should_throw_exceptions_when_borrowMedium_have_negative_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowMedium(-1);

        //THEN
        assertThrows(JamJarsException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowMedium_is_null() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Executable e = () -> jamJars.borrowMedium(null);

        //THEN
        assertThrows(NullPointerException.class, e);
    }

    @Test
    public void should_throw_exceptions_when_borrowMedium_have_larger_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowMedium = jamJars.borrowMedium(11);

        //THEN
        assertThat(borrowMedium).isEqualTo(10);
    }

    @Test
    public void should_throw_exceptions_when_borrowMedium_have_smaller_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowMedium = jamJars.borrowMedium(9);

        //THEN
        assertThat(borrowMedium).isEqualTo(9);
    }

    @Test
    public void should_throw_exceptions_when_borrowMedium_have_equals_value() {
        //GIVEN
        JamJars jamJars = new JamJars(10, 10, 10);

        //WHEN
        Integer borrowMedium = jamJars.borrowMedium(10);

        //THEN
        assertThat(borrowMedium).isEqualTo(10);
    }
}