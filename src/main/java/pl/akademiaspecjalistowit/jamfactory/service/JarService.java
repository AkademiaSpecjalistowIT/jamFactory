package pl.akademiaspecjalistowit.jamfactory.service;

import java.util.UUID;
import pl.akademiaspecjalistowit.jamfactory.model.JarOrderRequestDto;

public interface JarService {
    UUID orderJars(JarOrderRequestDto jarOrderRequestDto);
}
