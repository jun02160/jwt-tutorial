package jjun.server.jwttutorial.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "account")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseTimeEntity {

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column
    private String loginType;

    @Column
    private String kakaoName;  // 카카오 닉네임

    @Column
    private String nickname;  // 사용자 별명

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;   // TODO 회원가입 시 프로필 사진을 나중에 등록할 수 있도록 nullable 지정 (true가 defauslt)

    @ManyToMany
    @JoinTable(
            name = "account_authority",
            joinColumns = {@JoinColumn(name = "account_id", referencedColumnName = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;
}
