package pl.akademiaspecjalistowit.jamfactory.controller.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Getter
@Service
public class JarClientService {
    private final JarClient jarClient;

    public JarClientService(ObjectMapper objectMapper) {
        OkHttpClient httpClient = new OkHttpClient.Builder().build();
        this.jarClient = new Retrofit.Builder()
                .baseUrl("http://localhost:8081/api/vi/jar/")
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(httpClient)
                .build()
                .create(JarClient.class);
    }
}
