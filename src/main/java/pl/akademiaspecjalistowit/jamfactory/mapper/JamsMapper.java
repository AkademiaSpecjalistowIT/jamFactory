package pl.akademiaspecjalistowit.jamfactory.mapper;

import org.springframework.stereotype.Component;
import pl.akademiaspecjalistowit.jamfactory.entity.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.model.JamPlanProductionResponseDto;

@Component
public class JamsMapper {

    public JamPlanProductionEntity toEntity(JamPlanProductionRequestDto jamPlanProductionRequestDto, Integer limit) {
        return new JamPlanProductionEntity(jamPlanProductionRequestDto.getPlanDate(),
            jamPlanProductionRequestDto.getSmallJamJars(),
            jamPlanProductionRequestDto.getMediumJamJars(),
            jamPlanProductionRequestDto.getLargeJamJars(),
            limit);
    }

    public JamPlanProductionResponseDto toResponse(JamPlanProductionEntity jamPlanProductionEntity) {
        return new JamPlanProductionResponseDto(jamPlanProductionEntity.getPlanDate(),
                jamPlanProductionEntity.getSmallJamJars(),
                jamPlanProductionEntity.getMediumJamJars(),
                jamPlanProductionEntity.getLargeJamJars());
    }
}