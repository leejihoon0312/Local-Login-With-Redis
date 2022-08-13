package com.deadline826.bedi.Goal.Controller;

import com.deadline826.bedi.Goal.Domain.Dto.GoalDto;
import com.deadline826.bedi.Goal.Domain.Dto.GoalRequestDto;
import com.deadline826.bedi.Goal.Service.GoalService;
import com.deadline826.bedi.login.Domain.User;
import com.deadline826.bedi.login.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {


    private final UserService userService;
    private final GoalService goalService;

    @GetMapping
    public ResponseEntity<List<GoalDto>> showGoals(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date, HttpServletRequest request){

        // accessToken 으로부터 유저정보 불러오기
        User user = userService.getUserFromAccessToken();

        // 유저정보와 프론트에서 넘겨주는 오늘 날짜를 이용해 오늘의 목표 불러오기
        List<GoalDto> todayGoals = goalService.getTodayGoals(user,date);

        return ResponseEntity.ok().body(todayGoals);
    }


    @PostMapping("/success")
    public ResponseEntity<GoalDto> isSuccess(@RequestBody GoalRequestDto goalRequestDto) {

        User user = userService.getUserFromAccessToken();

        GoalDto goal = goalService.isSuccess(user, goalRequestDto);

        return ResponseEntity.ok().body(goal);
    }

}
