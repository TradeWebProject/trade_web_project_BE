package com.github.tradewebproject.repository.User;

import com.github.tradewebproject.Dto.User.UserDto;

import java.util.List;

public interface UserRepository {

    UserDto save(UserDto userDto);
    public List<UserDto> findAll();
    UserDto findByEmail(String email);
    public void deleteByEmail(String email);
}