package com.deadline826.bedi.point.service;

import com.deadline826.bedi.point.domain.Point;
import com.deadline826.bedi.point.repository.PointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PointServiceImpl implements PointService {

    @Autowired
    PointRepository pointRepository;

    @Override
    public Integer getAccumulatedPoint(Long userId) {
        try {
            Integer sum = pointRepository.getAccumulatedPointFromUser(userId).get(0);
            if (sum != null) return sum;
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        return 0;
    }

    @Override
    public void save(Point point) {
        pointRepository.save(point);
    }
}
