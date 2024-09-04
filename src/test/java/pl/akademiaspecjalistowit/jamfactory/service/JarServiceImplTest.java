package pl.akademiaspecjalistowit.jamfactory.service;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.akademiaspecjalistowit.jamfactory.dto.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.JarFactoryHttpClientException;
import pl.akademiaspecjalistowit.jamfactory.httpclient.JarFactoryHttpClient;

import java.time.LocalDate;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@ActiveProfiles("test")
class JarServiceImplTest {
    @Autowired
    private JarFactoryHttpClient jarFactoryHttpClient;

    @Test
    void orderJars() {
        //GIVEN
        LocalDate planDate = LocalDate.now().plusDays(1);
        Integer smallJamJars = 20000;
        Integer mediumJamJars = 20000;
        Integer largeJamJars = 20000;
        JarOrderRequestDto jarOrderRequestDto = new JarOrderRequestDto(planDate, smallJamJars, mediumJamJars, largeJamJars);

        //WHEN
        Executable e = () -> jarFactoryHttpClient.createJarOrder(jarOrderRequestDto);

        //THEN
        assertThrows(JarFactoryHttpClientException.class, e);
    }
}