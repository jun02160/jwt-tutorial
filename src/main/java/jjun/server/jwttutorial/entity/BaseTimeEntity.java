package jjun.server.jwttutorial.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // JPA Entity 클래스들이 BaseTimeEntity 를 상속하는 경우, 필드(createdAt, modifiedAt)들도 모두 컬럼으로 인식하도록 한다,
@EntityListeners(AuditingEntityListener.class)  // BastTimeEntity 클래스에 시간 데이터를 자동으로 매핑하여 값을 넣어주는 JPA Auditing 기능 포함
public abstract class BaseTimeEntity {

    @CreatedDate   // Entity 가 생성되어 저장될 때 시간이 자동 저장된다.
    private LocalDateTime createdAt;

    @LastModifiedDate  // 조회한 Entity 의 값을 변경할 때 최종 수정 시간이 자동 저장된다.
    private LocalDateTime modifiedAt;

    // TODO 나중에 추가할 클래스에 대해서도 모두 이 클래스를 상속받도록 지정
}
