package com.github.tradewebproject.repository.User;

import com.github.tradewebproject.Dto.User.UserDto;
import com.github.tradewebproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository  {

    UserDto save(UserDto userDto);
    public List<UserDto> findAll();
    UserDto findByEmail(String email);
    public void deleteByEmail(String email);
    Optional<User> findByEmail2(String email);

}