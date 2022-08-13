package com.deadline826.bedi.Goal.Domain;

import com.deadline826.bedi.login.Domain.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Double lat;   // 위도
    private Double lon;   // 경도

    private String title; // 제목

    @ColumnDefault("false")
    private Boolean success;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;             // 연관관게 주인
}
