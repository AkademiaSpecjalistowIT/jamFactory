package pl.akademiaspecjalistowit.jamfactory.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.stereotype.Component;
import pl.akademiaspecjalistowit.jamfactory.httpclient.JarHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Getter
@Component
public class JarClientConfiguration {
    private  JarHttpClient jarClient;

//    public JarClientConfiguration(ObjectMapper objectMapper) {
//        okhttp3.OkHttpClient httpClient = new ok;
//        this.jarClient = new Retrofit.Builder()
//                .baseUrl("http://localhost:8081/")
//                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
//                .client(httpClient)
//                .build()
//                .create(JarHttpClient.class);
//    }
}
