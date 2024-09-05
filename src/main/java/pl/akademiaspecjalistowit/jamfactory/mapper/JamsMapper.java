package pl.akademiaspecjalistowit.jamfactory.mapper;

import org.springframework.stereotype.Component;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionResponseDto;

@Component
public class JamsMapper {
    public JamPlanProductionEntity toEntity(JamPlanProductionRequestDto jamPlanProductionRequestDto) {
        return new JamPlanProductionEntity(jamPlanProductionRequestDto.getPlanDate(),
                jamPlanProductionRequestDto.getSmallJamJars(),
                jamPlanProductionRequestDto.getMediumJamJars(),
                jamPlanProductionRequestDto.getLargeJamJars());
    }

    public JamPlanProductionResponseDto toResponse(JamPlanProductionEntity jamPlanProductionEntity) {
        return new JamPlanProductionResponseDto(jamPlanProductionEntity.getPlanDate(),
                jamPlanProductionEntity.getSmallJamJars(),
                jamPlanProductionEntity.getMediumJamJars(),
                jamPlanProductionEntity.getLargeJamJars());
    }
}