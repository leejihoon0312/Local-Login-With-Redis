package com.deadline826.bedi.point.domain;

import com.deadline826.bedi.login.Domain.User;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table (name="point")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="point_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne (fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    private LocalDate date;

    private Integer reward;

    @Builder
    public Point(User user, Integer reward) {
        this.user = user;
        this.reward = reward;
    }

}