package pl.akademiaspecjalistowit.jamfactory.httpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import okhttp3.internal.http2.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionSystemException;
import pl.akademiaspecjalistowit.jamfactory.configuration.JarFactoryHttpConnections;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.JarFactoryHttpClientException;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.UUID;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@ActiveProfiles("test")
class JarFactoryHttpClientTest {
    @Autowired
    private JarFactoryHttpClient jarFactoryHttpClient;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private JarFactoryHttpConnections jarFactoryHttpConnections;

    @MockBean
    private JavaHttpClient javaHttpClient;

//    @Test
//    void should_create_JarOrder() throws Exception {
//        //GIVEN
//        LocalDate planDate = LocalDate.now().plusDays(1);
//        Integer smallJamJars = 1000;
//        Integer mediumJamJars = 1000;
//        Integer largeJamJars = 1000;
//        JarOrderRequestDto jarOrderRequestDto = new JarOrderRequestDto(planDate, smallJamJars, mediumJamJars, largeJamJars);
//        String jarOrderRequestJson = "{}";  // пример JSON
//
//        UUID expectedUUID = UUID.randomUUID();
//        HttpResponse<String> mockResponse = mock(HttpResponse.class);
//
//        when(objectMapper.writeValueAsString(jarOrderRequestDto)).thenReturn(jarOrderRequestJson);
//        when(jarFactoryHttpConnections.getOrders()).thenReturn("http://mockurl.com/orders");
//        when(javaHttpClient.httpPost(jarOrderRequestJson, "http://mockurl.com/orders")).thenReturn(mockResponse);
//        when(mockResponse.body()).thenReturn(expectedUUID.toString());
//        when(objectMapper.readValue(expectedUUID.toString(), UUID.class)).thenReturn(expectedUUID);
//
//        //WHEN
//        UUID result = jarFactoryHttpClient.createJarOrder(jarOrderRequestDto);
//
//        //THEN
//        assertEquals(expectedUUID, result);
//        verify(javaHttpClient, times(1)).httpPost(jarOrderRequestJson, "http://mockurl.com/orders");
//    }

//    @Test
//    void should_throw_exception_when_error_occurs() throws JsonProcessingException {
//        //GIVEN
//        LocalDate planDate = LocalDate.now().plusDays(1);
//        Integer smallJamJars = 20000;
//        Integer mediumJamJars = 20000;
//        Integer largeJamJars = 20000;
//        JarOrderRequestDto jarOrderRequestDto = new JarOrderRequestDto(planDate, smallJamJars, mediumJamJars, largeJamJars);
//
//        when(objectMapper.writeValueAsString(jarOrderRequestDto)).thenThrow(new JarFactoryHttpClientException("Serialization error", ErrorCode.INSUFFICIENT_JARS));
//
//        //WHEN
//        Executable e = () -> jamPlanProductionService.addProductionPlan(jamPlanProductionRequestDto);
//        JarFactoryHttpClientException exception = assertThrows(JarFactoryHttpClientException.class, () -> {
//            jarFactoryHttpClient.createJarOrder(jarOrderRequestDto);
//        });
//
//        //THEN
//        assertThrows(JarFactoryHttpClientException.class, e);
////        assertEquals("Nie udało się złożyć zamówienia na słoiki", exception.getMessage());
//    }
}