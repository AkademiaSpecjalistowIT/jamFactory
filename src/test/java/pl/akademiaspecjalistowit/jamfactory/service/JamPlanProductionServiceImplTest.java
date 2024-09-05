package pl.akademiaspecjalistowit.jamfactory.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.configuration.EmbeddedPostgresConfiguration;
import pl.akademiaspecjalistowit.jamfactory.dto.JamListPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarException;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void should_create_product_plan() {
        //GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE,
                CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS);

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
    void should_find_product_plan() {
        //GIVEN
        LocalDate today = LocalDate.of(2024,9,5);
        LocalDate todayPlusSevenDays = LocalDate.of(2024,9,11);

        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE,
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