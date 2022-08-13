package com.deadline826.bedi.Goal.Service;

import com.deadline826.bedi.Goal.Domain.Dto.GoalDto;
import com.deadline826.bedi.Goal.Domain.Dto.GoalRequestDto;
import com.deadline826.bedi.Goal.Domain.Goal;
import com.deadline826.bedi.Goal.exception.OutRangeOfGoalException;
import com.deadline826.bedi.Goal.exception.WrongGoalIDException;
import com.deadline826.bedi.Goal.repository.GoalRepository;
import com.deadline826.bedi.login.Domain.User;
import com.deadline826.bedi.point.domain.Point;
import com.deadline826.bedi.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService{
    private final GoalRepository goalRepository;
    private final PointRepository pointRepository;
    private final ModelMapper modelMapper;

    //추가
    @Override
    public List<GoalDto> getTodayGoals(User user, LocalDate date){
        List<Goal> goalsOrderByTitleAsc = goalRepository.findByUserAndDateOrderByTitleAsc(user,date);
        List<GoalDto> goalDtosList = goalsOrderByTitleAsc.stream()
                .map(goal -> modelMapper.map(goal, GoalDto.class))
                .collect(Collectors.toList());
        return goalDtosList;
    }

    private static Double distance(Double lat1, Double lon1, Double lat2, Double lon2) {

        Double theta = lon1 - lon2;
        Double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return dist * 1609.344;

    }

    // This function converts decimal degrees to radians
    private static Double deg2rad(Double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static Double rad2deg(Double rad) {
        return (rad * 180 / Math.PI);
    }

    @Override
    public GoalDto isSuccess(User user, GoalRequestDto goalRequestDto) {

            Goal goal = goalRepository.findById(goalRequestDto.getGoalId())
                    .orElseThrow(() -> new WrongGoalIDException("잘못된 목표 아이디 입니다."));

            Double goalLat = goal.getLat();
            Double goalLon = goal.getLon();
            Double nowLat = goalRequestDto.getNowLat();
            Double nowLon = goalRequestDto.getNowLon();

            Double distance = distance(goalLat, goalLon, nowLat, nowLon);

            // 사용자가 목표 범위(50m) 이내에 존재 안함
            if (Double.compare(distance, 50.0) > 0) throw new OutRangeOfGoalException("목표 범위로부터 너무 멉니다.");

            Point point = Point.builder()
                    .user(user)
                    .reward(20)
                    .build();
            pointRepository.save(point);
            goal.setSuccess(true);

            return modelMapper.map(goal, GoalDto.class);
    }

}
