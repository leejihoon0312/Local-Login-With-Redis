package com.deadline826.bedi.point.service;

import com.deadline826.bedi.point.domain.Point;

public interface PointService {

    Integer getAccumulatedPoint(Long userId);
    void save(Point point);

}