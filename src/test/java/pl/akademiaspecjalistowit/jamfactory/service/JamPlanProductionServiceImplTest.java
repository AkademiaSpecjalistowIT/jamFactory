package pl.akademiaspecjalistowit.jamfactory.service;

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
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarException;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
    private JamsMapper jamsMapper;

    @Test
    void should_create_product_plan() throws JarException {
        //GIVEN
        JamPlanProductionRequestDto jamPlanProductionRequestDto = new JamPlanProductionRequestDto(CORRECT_PLAN_DATE, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS, CORRECT_QUANTITY_JAM_JARS);
        jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);

        //WHEN
        List<JamPlanProductionEntity> all = jamPlanProductionRepository.findAll();

        //THEN
        assertThat(all).isNotNull();
        assertThat(all.size()).isEqualTo(1);
    }
}