package com.deadline826.bedi.Goal.Domain.Dto;

import lombok.Getter;

@Getter
public class GoalRequestDto {
    private Long goalId;
    private Double nowLat;
    private Double nowLon;
}
