package jjun.server.jwttutorial.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_token")
@Getter @Setter
@NoArgsConstructor
public class RefreshToken {

    // DB의 인덱스 용도로 저장
    @Id
    @Column(name = "rt_key", nullable = false)
    private Long key;  // user의 ID 값이 들어감

    @Column(name = "rt_value", nullable = false)
    private String value;  // Refresh Token String

    @Builder
    public RefreshToken(Long key, String value) {
        this.key = key;
        this.value = value;
    }

    public RefreshToken updateToken(String token) {
        this.value = token;
        return this;
    }
}
