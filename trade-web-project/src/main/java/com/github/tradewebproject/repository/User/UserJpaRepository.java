package com.github.tradewebproject.repository.User;

import com.github.tradewebproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
    Jpa를 이용한 Repository
*/

public interface UserJpaRepository extends JpaRepository<User, Long> {

    //email을 통해 사용자 정보를 가져옴
    User findByEmail(String email);
    Optional<User> findById(Long userId);
    User findByUserId(Long userId);

}
