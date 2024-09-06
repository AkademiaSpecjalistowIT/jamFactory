package pl.akademiaspecjalistowit.jamfactory.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.configuration.EmbeddedPostgresConfiguration;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
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

    private final LocalDate CORRECT_PLAN_DATE = LocalDate.of(2024, 9, 29);
    private final Integer CORRECT_QUANTITY_JAM_JARS = 100;
    private final Integer INCORRECT_QUANTITY_JAM_JARS = -100;

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
        // GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(
                CORRECT_PLAN_DATE, 0, 0, CORRECT_QUANTITY_JAM_JARS
        );
        // WHEN
        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
        // THEN
        List<JamPlanProductionEntity> all = jamPlanProductionRepository.findAll();
        assertThat(all).isNotNull();
        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    void should_distribute_excess_when_limit_exceeded() {
        // GIVEN
        JamPlanProductionRequestDto requestDto1 = new JamPlanProductionRequestDto(
                LocalDate.of(2024, 9, 7), 0, 0, 1500
        );
        jamPlanProductionService.addProductionPlan(requestDto1);

        JamPlanProductionRequestDto requestDto2 = new JamPlanProductionRequestDto(
                LocalDate.of(2024, 9, 9), 0, 0, 600
        );
        LocalDate today = LocalDate.now();

        jamPlanProductionService.addProductionPlan(requestDto2);
        //then
        List<JamPlanProductionEntity> allByPlanDateBetween =
                jamPlanProductionRepository.findAllByPlanDateBetween(today, requestDto2.getPlanDate());
        double plannedFactoryProductionInKg = allByPlanDateBetween.stream()
                .mapToDouble(JamPlanProductionEntity::getTotalJamWeight)
                .sum();
        assertThat(plannedFactoryProductionInKg).isEqualTo(2100);
    }

    @Test
    void should_throw_exception_when_limit_exceeded_and_not_exist_possibility_distribute_because_exceeds_more_then_maxCapacity() {
        // GIVEN
        JamPlanProductionRequestDto requestDto1 = new JamPlanProductionRequestDto(
                LocalDate.of(2024, 9, 7), 0, 0, 1500
        );

        jamPlanProductionService.addProductionPlan(requestDto1);

        JamPlanProductionRequestDto requestDto2 = new JamPlanProductionRequestDto(
                LocalDate.of(2024, 9, 7), 0, 0, 2600
        );
        LocalDate today = LocalDate.now();

        //when&then
        ProductionException thrownException = assertThrows(
                ProductionException.class,
                () -> jamPlanProductionService.addProductionPlan(requestDto2));


        assertThat(thrownException.getMessage()).contains("Przekroczono limit produkcyjny");

    }
}