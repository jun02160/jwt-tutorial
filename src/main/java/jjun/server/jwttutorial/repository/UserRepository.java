package jjun.server.jwttutorial.repository;

import jjun.server.jwttutorial.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 엔티티에 매핑되는 레포지토리
 * - JpaRepository를 extends 하면 기본적으로 findAll, save 등의 메소드를 사용할 수 있다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // username으로 User 정보를 가져올 때, 권한 정보도 함께 가져오게 해주는 메소드
    @EntityGraph(attributePaths = "authorities")    // @EntityGraph: 쿼리가 수행될 때, Lazy 조회가 아닌 Eager 조회로 authorities 정보를 가져옴
    Optional<User> findOneWithAuthoritiesByUsername(String username);
}
