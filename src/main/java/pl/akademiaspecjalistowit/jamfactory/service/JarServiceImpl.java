package pl.akademiaspecjalistowit.jamfactory.service;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.dto.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.httpclient.JarFactoryHttpClient;

@Service
@AllArgsConstructor
public class JarServiceImpl implements JarService {

    private final JarFactoryHttpClient jarFactoryHttpClient;

    @Override
    public UUID orderJars(JarOrderRequestDto jarOrderRequestDto) {
        return jarFactoryHttpClient.createJarOrder(jarOrderRequestDto);
    }
}
