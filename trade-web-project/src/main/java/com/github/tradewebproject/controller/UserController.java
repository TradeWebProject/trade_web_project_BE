package com.github.tradewebproject.controller;

import com.github.tradewebproject.Dto.Jwt.ResponseToken;
import com.github.tradewebproject.Dto.User.NewUserDto;
import com.github.tradewebproject.Dto.User.UserDto;
import com.github.tradewebproject.Dto.Jwt.Token;
import com.github.tradewebproject.service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("/api") //내부에 선언한 메서드의 URL 리소스 앞에 @RequestMapping의 값이 공통 값으로 추가됨.
@RequiredArgsConstructor
@RestController //사용자 요청을 제어하는 controller 클래스
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signup")
    public ResponseEntity<?> join(
            @RequestParam("email") String email,
            @RequestParam("password") String userPassword,
            @RequestParam("nickname") String userNickname,
            @RequestParam("phone") String userPhone,
            @RequestParam("interests") String userInterests,
            @RequestParam("userImg") MultipartFile userImg) {

        try {
            NewUserDto newUserDto = NewUserDto.builder()
                    .email(email)
                    .userPassword(userPassword)
                    .userNickname(userNickname)
                    .userPhone(userPhone)
                    .userInterests(userInterests)
                    .userImg(userImg)
                    .build();

            // 유효성 검사
            if (!isValidEmail(newUserDto.getEmail())) {
                log.info("이메일 형식에 맞게 입력하세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 형식에 맞게 입력하세요.");
            }

            if (!isValidPassword(newUserDto.getUserPassword())) {
                log.info("비밀번호 형식에 맞게 입력해주세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 형식에 맞게 입력해주세요.");
            }

            if (!isValidPhone(newUserDto.getUserPhone())) {
                log.info("핸드폰 입력 형식에 맞게 입력해주세요.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("핸드폰 입력 형식에 맞게 입력해주세요.");
            }

            // 사용자 등록
            UserDto savedUser = userService.register(newUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(savedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody UserDto login) {
        try {
            String email = login.getEmail();
            String password = login.getUserPassword();
            Token token = userService.login(email, password);
            ResponseEntity.ok().body(ResponseToken.of(token));

            return new ResponseEntity<>("로그인 되었습니다.", HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/users/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @RequestBody UserDto userDto) {
        String res = userService.logout(request, userDto.getEmail());
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/users/unregister/{email}")
    public ResponseEntity<String> unregister(@PathVariable String email) {
        userService.unregister(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원 탈퇴 되었습니다.");
    }

    //이메일 유효성 검사
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    //비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        String passwordRegex = "(?=.*[0-9])(?=.*[A-Za-z]).{8,20}$";
        return password.matches(passwordRegex);
    }

    //핸드폰 번호 유효성 검사
    private boolean isValidPhone (String user_phone) {
        String phoneRegex = "^01[0|1|6|7|8|9][0-9]{7,8}$";
        return user_phone.matches(phoneRegex);
    }

}