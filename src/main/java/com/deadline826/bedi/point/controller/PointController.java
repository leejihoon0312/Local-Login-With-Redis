package com.deadline826.bedi.point.controller;

import com.deadline826.bedi.login.Service.UserService;
import com.deadline826.bedi.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/point")
public class PointController {

    @Autowired
    PointService pointService;
    @Autowired
    UserService userService;

    @GetMapping
    public Integer getAllPoint() {
        Long userId = userService.getUserFromAccessToken().getId();
        return pointService.getAccumulatedPoint(userId);
    }

}
