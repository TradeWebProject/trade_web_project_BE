package com.github.tradewebproject.repository.User;

import com.github.tradewebproject.Dto.User.UserDto;
import com.github.tradewebproject.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository //db 연동을 처리하는 DAO 클래스
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{

    private final UserJpaRepository userJpaRepository;

    //회원 저장
    @Override
    public UserDto save(UserDto userDto) {
        return userJpaRepository.save(User.from(userDto)).toDTO();
    }

    //회원 전체 조회
    @Override
    public List<UserDto> findAll() {
        List<User> memberEntities = userJpaRepository.findAll();

        return memberEntities.stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    //회원 이메일 조회
    @Override
    public UserDto findByEmail(String email) {
        User userDto = userJpaRepository.findByEmail(email);
        if (userDto == null) {
            return null;
        } else {
            return userDto.toDTO();
        }
    }
    @Override
    public Optional<User> findByEmail2(String email) {
        return Optional.ofNullable(userJpaRepository.findByEmail(email));
    }

    @Override
    public void deleteByEmail(String email) {
        User userEntity = userJpaRepository.findByEmail(email);
        if (userEntity != null) {
            userJpaRepository.delete(userEntity);
        } else {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
    }


}