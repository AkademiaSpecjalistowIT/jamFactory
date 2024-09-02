package pl.akademiaspecjalistowit.jamfactory.service;

import java.io.IOException;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.dto.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.exception.JarException;
import pl.akademiaspecjalistowit.jamfactory.httpclient.JarHttpClient;
import retrofit2.Response;

@Service
@AllArgsConstructor
public class JarServiceImpl implements JarService {

    private JarHttpClient jarHttpClient;

    @Override
    public UUID orderJars(JarOrderRequestDto jarOrderRequestDto) {

        try {
            Response<UUID> response = jarHttpClient.createJarOrder(jarOrderRequestDto).execute();
            if (!response.isSuccessful()) {
                throw new JarException("Nie udało się zamówić słoików na realizację planu produkcyjnego");
            }
            return response.body();
        } catch (IOException e) {
            throw new JarException("Nie udało się zamówić słoików na realizację planу produkcyjnego");
        }
    }
}
