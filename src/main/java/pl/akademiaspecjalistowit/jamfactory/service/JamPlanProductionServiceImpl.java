package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.util.UUID;

@AllArgsConstructor
@Service
public class JamPlanProductionServiceImpl implements JamPlanProductionService {
    private final JamPlanProductionRepository jamPlanProductionRepository;
    private final JamsMapper jamsMapper;

    @Override
    public UUID addProductionPlan(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        JamPlanProductionEntity entity = jamsMapper.toEntity(jamPlanProductionRequestDto);
        jamPlanProductionRepository.save(entity);
        return entity.getPlanId();
    }
}
