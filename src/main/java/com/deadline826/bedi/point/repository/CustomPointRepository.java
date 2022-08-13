package com.deadline826.bedi.point.repository;

import java.util.List;

public interface CustomPointRepository {

    List<Integer> getAccumulatedPointFromUser(Long userId);

}