package pl.akademiaspecjalistowit.jamfactory.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;
import pl.akademiaspecjalistowit.jamfactory.model.JamListPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.model.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
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

    @MockBean
    private JarService jarService;


    @Test
    void should_create_product_plan() {
        //GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE,
                CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS);

        JarOrderRequestDto jarOrderRequestDto =
                new JarOrderRequestDto(jamPlanProductionRequestDto.getPlanDate().plusDays(1),
                        jamPlanProductionRequestDto.getSmallJamJars(), jamPlanProductionRequestDto.getMediumJamJars(),
                        jamPlanProductionRequestDto.getLargeJamJars());

        when(jarService.orderJars(jarOrderRequestDto)).thenReturn(UUID.randomUUID());

        //WHEN
        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
    }


    @Test
    void should_throw_production_exception_when_invalid_capacity() {
        //GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE,
                LARGE_QUANTITY_JAM_JARS, LARGE_QUANTITY_JAM_JARS, LARGE_QUANTITY_JAM_JARS);

        //WHEN
        Executable e = () -> jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        //THEN
        assertThrows(ProductionException.class, e);
    }

    @Test
    void should_distribute_excess_when_limit_exceeded() {
        // GIVEN
        JamPlanProductionRequestDto requestDto1 = new JamPlanProductionRequestDto(
                LocalDate.now(), 0, 0, 1500
        );
        jamPlanProductionService.addProductionPlan(requestDto1);

        JamPlanProductionRequestDto requestDto2 = new JamPlanProductionRequestDto(
                LocalDate.now().plusDays(2), 0, 0, 600
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
                LocalDate.now(), 0, 0, 1500
        );

        jamPlanProductionService.addProductionPlan(requestDto1);

        JamPlanProductionRequestDto requestDto2 = new JamPlanProductionRequestDto(
                LocalDate.now(), 0, 0, 2600
        );
        LocalDate today = LocalDate.now();

        //when&then
        ProductionException thrownException = assertThrows(
                ProductionException.class,
                () -> jamPlanProductionService.addProductionPlan(requestDto2));


        assertThat(thrownException.getMessage()).contains("Przekroczono limit produkcyjny");

    }

    @Test
    void should_throw_production_exception_with_incorrect_capacity() {
        //GIVEN
        LocalDate plan_date = LocalDate.now().plusDays(1);
        Integer jars = -100;

        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(plan_date,
                jars, jars, jars);

        //WHEN
        Executable e = () -> jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        //THEN
        assertThrows(TransactionSystemException.class, e);
    }

    @Test
    void should_throw_exception_with_add_new_plan_when_capacity_was_full() {
        //GIVEN
        LocalDate plan_date = LocalDate.now().plusDays(5);
        LocalDate plan_date2 = LocalDate.now().plusDays(2);

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
    @Test
    void should_find_product_plan() {
        //GIVEN
        LocalDate today = LocalDate.now();//of(2024,9,5);
        LocalDate todayPlusSevenDays = LocalDate.now().plusDays(7);//of(2024,9,11);

        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(today,
                CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS);

        JamPlanProductionRequestDto jamPlanProductionRequestDtoAfterSevenDays = new JamPlanProductionRequestDto(todayPlusSevenDays,
                CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS);

        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDtoAfterSevenDays);

        //WHEN
        JamListPlanProductionResponseDto listPlanProduction = jamPlanProductionService.getPlanProduction();

        //THEN
        assertThat(listPlanProduction).isNotNull();
        assertThat(listPlanProduction.getListPlans().size()).isEqualTo(2);
        assertThat(listPlanProduction.getSumSmallJamJars()).isEqualTo(200);
        assertThat(listPlanProduction.getSumMediumJamJars()).isEqualTo(200);
        assertThat(listPlanProduction.getSumLargeJamJars()).isEqualTo(200);
        assertThat(listPlanProduction.getListPlans().getFirst().getPlanDate()).isEqualTo(today);
        assertThat(listPlanProduction.getListPlans().get(1).getPlanDate()).isEqualTo(todayPlusSevenDays);
    }
}