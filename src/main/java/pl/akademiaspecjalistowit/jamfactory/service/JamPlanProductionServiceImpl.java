package pl.akademiaspecjalistowit.jamfactory.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.akademiaspecjalistowit.jamfactory.JamPlanProductionEntity;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionRequestDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JamListPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.dto.JamPlanProductionResponseDto;
import pl.akademiaspecjalistowit.jamfactory.mapper.JamsMapper;
import pl.akademiaspecjalistowit.jamfactory.repositories.JamPlanProductionRepository;

import java.time.LocalDate;
import java.util.List;
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

    @Override
    public JamListPlanProductionResponseDto getPlanProduction() {
        LocalDate today = LocalDate.now();
        LocalDate todayPlusSevenDays = LocalDate.now().plusDays(7);

        List<JamPlanProductionResponseDto> list = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .map(entity -> jamsMapper.toResponse(entity))
                .toList();

        int  sumSmallJamJars = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .mapToInt(plan -> plan.getSmallJamJars())
                .sum();

        int  sumMediumJamJars = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .mapToInt(plan -> plan.getMediumJamJars())
                .sum();

        int  sumLargeJamJars = jamPlanProductionRepository.findAll().stream()
                .filter(plan -> !plan.getPlanDate().isBefore(today) && !plan.getPlanDate().isAfter(todayPlusSevenDays))
                .mapToInt(plan -> plan.getLargeJamJars())
                .sum();

        return new JamListPlanProductionResponseDto(list,sumSmallJamJars,sumMediumJamJars,sumLargeJamJars);
    }
}
