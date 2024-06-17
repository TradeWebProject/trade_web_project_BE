package com.github.tradewebproject.service.User;

import com.github.tradewebproject.Dto.User.EditUserDto;
import com.github.tradewebproject.Dto.User.NewUserDto;
import com.github.tradewebproject.Dto.User.UserDto;
import com.github.tradewebproject.Dto.User.getUserDto;
import com.github.tradewebproject.controller.UserResponse;
import com.github.tradewebproject.Dto.Jwt.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface  UserService {


    String encodePassword(String password);
    boolean matchesPassword(String rawPassword, String encodedPassword);

    UserDto register(NewUserDto userDto) throws IOException;
    void unregister(String email);

    Token login(String email, String pw) throws Exception;
    public String logout(HttpServletRequest request, String email);

    UserResponse getByEmail(String email);
    UserDto editUser(Long userId, EditUserDto editUserDto) throws IOException;
    ResponseEntity<?> getUserById(Long userId);



}