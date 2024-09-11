package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.model.JarOrderRequestDto;
import pl.akademiaspecjalistowit.jamfactory.httpclient.JarFactoryHttpClient;

import java.util.UUID;

@Service
@AllArgsConstructor
public class JarServiceImpl implements JarService {

    private final JarFactoryHttpClient jarFactoryHttpClient;

    @Override
    public UUID orderJars(JarOrderRequestDto jarOrderRequestDto) {
        return jarFactoryHttpClient.createJarOrder(jarOrderRequestDto);
    }
}
