package com.deadline826.bedi.Goal.Service;

import com.deadline826.bedi.Goal.Domain.Dto.GoalDto;
import com.deadline826.bedi.Goal.Domain.Dto.GoalRequestDto;
import com.deadline826.bedi.login.Domain.User;

import java.time.LocalDate;
import java.util.List;

public interface GoalService {

    List<GoalDto> getTodayGoals(User user, LocalDate date); // 오늘날짜의 목표 불러오기

    GoalDto isSuccess(User user, GoalRequestDto goalRequestDto);

}
