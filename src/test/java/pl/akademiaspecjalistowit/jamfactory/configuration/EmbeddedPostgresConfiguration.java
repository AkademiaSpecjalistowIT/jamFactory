package pl.akademiaspecjalistowit.jamfactory.configuration;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.testcontainers.utility.DockerImageName;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;
import pl.akademiaspecjalistowit.jamfactory.service.JamPlanProductionService;
import pl.akademiaspecjalistowit.jamfactory.service.JamPlanProductionServiceImpl;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableJpaRepositories(basePackageClasses = JamPlanProductionRepository.class)
@EntityScan(basePackageClasses = JamPlanProductionEntity.class)
public class EmbeddedPostgresConfiguration {
    private static EmbeddedPostgres embeddedPostgres;

    @Bean
    public DataSource dataSource() throws IOException {
        embeddedPostgres = EmbeddedPostgres.builder()
                .setImage(DockerImageName.parse("postgres:14.1"))
                .start();
        return embeddedPostgres.getPostgresDatabase();
    }

    @Bean
    public JamPlanProductionService jamPlanProductionService(JamPlanProductionRepository jamPlanProductionRepository,
                                                             JamsMapper jamsMapper, ApiProperties apiProperties) {
        return new JamPlanProductionServiceImpl(jamPlanProductionRepository, jamsMapper, apiProperties);
    }

    @Bean
    public JamsMapper jamsMapper(){
        return new JamsMapper();
    }

    public static class EmbeddedPostgresExtension implements AfterAllCallback {
        @Override
        public void afterAll(ExtensionContext context) throws Exception {
            if (embeddedPostgres == null) {
                return;
            }
            embeddedPostgres.close();
        }
    }
}