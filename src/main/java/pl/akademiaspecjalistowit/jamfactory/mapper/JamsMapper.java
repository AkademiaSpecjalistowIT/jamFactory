package pl.akademiaspecjalistowit.jamfactory.mapper;

import org.springframework.stereotype.Component;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;

@Component
public class JamsMapper {
    public JamPlanProductionEntity toEntity(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        return new JamPlanProductionEntity(jamPlanProductionRequestDto.getPlanDate(),
                jamPlanProductionRequestDto.getSmallJamJars(),
                jamPlanProductionRequestDto.getMediumJamJars(),
                jamPlanProductionRequestDto.getLargeJamJars());
    }
}