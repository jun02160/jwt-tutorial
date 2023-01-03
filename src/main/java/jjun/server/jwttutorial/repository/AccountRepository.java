package jjun.server.jwttutorial.repository;

import jjun.server.jwttutorial.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 이메일로 DB 에 동일한 계정이 있는지 조회
    Optional<Account> findByEmail(String email);

    // 중복가입을 방지하기 위해 이메일 존재 여부 판단
    boolean existsByEmail(String email);
}
