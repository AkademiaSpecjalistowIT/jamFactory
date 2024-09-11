package pl.akademiaspecjalistowit.jamfactory.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.akademiaspecjalistowit.jamfactory.configuration.JarFactoryHttpConnections;
import pl.akademiaspecjalistowit.jamfactory.model.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.JarFactoryHttpClientException;

@Component
@AllArgsConstructor
public class JarFactoryHttpClient {

    private final ObjectMapper objectMapper;
    private final JarFactoryHttpConnections jarFactoryHttpConnections;
    private final JavaHttpClient javaHttpClient;

    public UUID createJarOrder(JarOrderRequestDto jarOrderRequestDto) {
        try {
            String jarOrderRequestJson = objectMapper.writeValueAsString(jarOrderRequestDto);
            HttpResponse<String> response = javaHttpClient.httpPost(jarOrderRequestJson, jarFactoryHttpConnections.getOrders());
            return objectMapper.readValue(response.body(), UUID.class);
        } catch (Exception e) {
            throw new JarFactoryHttpClientException("Nie udało się złożyć zamówienia na słoiki", e);
        }
    }
}
