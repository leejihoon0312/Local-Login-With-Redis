package com.deadline826.bedi.point.repository;

import com.deadline826.bedi.login.Domain.QUser;
import com.deadline826.bedi.point.domain.QPoint;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RequiredArgsConstructor
public class PointRepositoryImpl implements CustomPointRepository {

    private final JPAQueryFactory queryfactory;
    private final QPoint point = QPoint.point;
    private final QUser user = QUser.user;

    @Override
    public List<Integer> getAccumulatedPointFromUser(Long userId) {
        return queryfactory.select(point.reward.sum())
                .from(point)
                .where(point.user.id.eq(userId))
                .fetch();
    }

}