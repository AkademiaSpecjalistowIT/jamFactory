package pl.akademiaspecjalistowit.jamfactory.service;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.exception.JarFactoryHttpClientException;
import pl.akademiaspecjalistowit.jamfactory.exception.ProductionException;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

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

    @MockBean
    private JarService jarService;

    @AfterEach
    void tearDown() {
        jamPlanProductionRepository.deleteAll();
    }

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
