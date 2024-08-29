package pl.akademiaspecjalistowit.jamfactory.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.akademiaspecjalistowit.jamfactory.configuration.EmbeddedPostgresConfiguration;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@ActiveProfiles("test")
class JamPlanProductionServiceImplTest {

    private final LocalDate CORRECT_PLAN_DATE = LocalDate.now();
    private final Integer CORRECT_QUANTITY_JAM_JARS = 100;
    private final Integer INCORRECT_QUANTITY_JAM_JARS = -100;
    private final Integer LARGE_QUANTITY_JAM_JARS = 20000;

    @Autowired
    private JamPlanProductionService jamPlanProductionService;

    @Autowired
    private JamPlanProductionRepository jamPlanProductionRepository;

    @AfterEach
    void tearDown() {
        jamPlanProductionRepository.deleteAll();
    }

    @Test
    void should_create_product_plan() {
        //GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE,
                CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS);

        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        //WHEN
        List<JamPlanProductionEntity> all = jamPlanProductionRepository.findAll();

        //THEN
        assertThat(all).isNotNull();
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).getPlanDate()).isEqualTo(CORRECT_PLAN_DATE);
        assertThat(all.get(0).getSmallJamJars()).isEqualTo(CORRECT_QUANTITY_JAM_JARS);
        assertThat(all.get(0).getMediumJamJars()).isEqualTo(CORRECT_QUANTITY_JAM_JARS);
        assertThat(all.get(0).getLargeJamJars()).isEqualTo(CORRECT_QUANTITY_JAM_JARS);
    }

    @Test
    void should_throw_exception_when_invalid_capacity() {
        //GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE,
                LARGE_QUANTITY_JAM_JARS, LARGE_QUANTITY_JAM_JARS, LARGE_QUANTITY_JAM_JARS);

        //WHEN
        Executable e = () -> jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        //THEN
        assertThrows(ProductionException.class, e);
    }

    @Test
    void should_throw_exception_with_incorect_capacity() {
        //GIVEN
        LocalDate plan_date = LocalDate.now().plusDays(1);
        Integer jars = 1000;

        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(plan_date,
                jars, jars, jars);

        //WHEN
        Executable e = () -> jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        //THEN
        assertThrows(ProductionException.class, e);
    }

    @Test
    void should_throw_exception_with_add_new_plan_when_capacity_was_full() {
        //GIVEN
        LocalDate plan_date = LocalDate.now().plusDays(1);
        LocalDate plan_date2 = LocalDate.now();

        Integer jars_s = 5000;
        Integer jars_m = 5000;
        Integer jars_l = 5000;
        Integer jars = 7000;

        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(plan_date,
                jars_s, jars_m, jars);

        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        JamPlanProductionRequestDto jamPlanProductionRequestDto2 = new JamPlanProductionRequestDto(plan_date2,
                jars_s, jars_m, jars_l);

        //WHEN
        Executable e = () -> jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto2);

        //THEN
        assertThrows(ProductionException.class, e);
    }
}
