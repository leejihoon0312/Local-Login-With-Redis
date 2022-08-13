package com.deadline826.bedi.login.Domain;

import com.deadline826.bedi.Goal.Domain.Goal;
import com.deadline826.bedi.Token.Domain.RefreshToken;
import com.deadline826.bedi.point.domain.Point;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Setter
public class User {

    @Id
    @Column(name="user_id")
    private Long id;       // 카카오가 넘겨주는 랜덤 값

    private String username;   // 사용자 이름

    private String password;  //인코딩 된 값

    private String email;  //카카오 이메일

    private String phone;




//    @Enumerated(EnumType.STRING)
//    private Authority authority;

    @OneToOne
    @JoinColumn (name = "refresh_id")
    private RefreshToken refreshToken;


    public void updateRefreshToken(RefreshToken newToken) {
        this.refreshToken = newToken;
    }

    @OneToMany(mappedBy = "user")
    private List<Goal> goals=new ArrayList<>();   // 양방향 매핑 (테이블에는 표시 안됨)

    @OneToMany(mappedBy = "user")
    private List<Point> points = new ArrayList<>();

}
