package com.github.tradewebproject.controller.User;

import com.github.tradewebproject.Dto.Jwt.ResponseToken;
import com.github.tradewebproject.Dto.User.EditUserDto;
import com.github.tradewebproject.Dto.User.NewUserDto;
import com.github.tradewebproject.Dto.User.UserDto;
import com.github.tradewebproject.Dto.Jwt.Token;
import com.github.tradewebproject.service.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequestMapping("/api") //내부에 선언한 메서드의 URL 리소스 앞에 @RequestMapping의 값이 공통 값으로 추가됨.
@RequiredArgsConstructor
@RestController //사용자 요청을 제어하는 controller 클래스
public class UserController {

    private final UserService userService;

    @PostMapping("/users/signup")
    @Operation(summary = "유저 회원가입", description = "이메일, 비밀번호, 닉네임, 휴대폰번호, 관심분야, 프로필 사진을 넣고 회원가입합니다.")
    public ResponseEntity<?> join(
            @RequestParam("email") String email,
            @RequestParam("password") String userPassword,
            @RequestParam("nickname") String userNickname,
            @RequestParam("phone") String userPhone,
            @RequestParam("interests") String userInterests,
            @RequestParam("userImg") MultipartFile userImg) {

        List<String> allowedInterests = Arrays.asList("전자기기", "의류", "가전", "문구", "도서", "신발", "여행용품", "스포츠");

        try {
            List<String> interestsList = Arrays.asList(userInterests.split(","));
            if (interestsList.size() > 3) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관심사는 최대 3개까지 입력할 수 있습니다.");
            }
            for (String interest : interestsList) {
                if (!allowedInterests.contains(interest)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관심사는 전자기기, 의류, 가전, 문구, 도서, 신발, 여행용품, 스포츠 중에서 선택해주세요.");
                }
            }

            NewUserDto newUserDto = NewUserDto.builder()
                    .email(email)
                    .userPassword(userPassword)
                    .userNickname(userNickname)
                    .userPhone(userPhone)
                    .userInterests(interestsList)
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
    @Operation(summary = "유저 로그인", description = "유저 이메일, 비밀번호를 이용하여 로그인합니다.")
    public ResponseEntity<?> login(@RequestBody UserDto login) {
        try {
            String email = login.getEmail();
            String password = login.getUserPassword();
            Token token = userService.login(email, password);
            return ResponseEntity.ok().body(ResponseToken.of(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/users/logout")
    @Operation(summary = "유저 로그아웃", description = "해당 이메일을 가진 유저를 로그아웃합니다.")
    public ResponseEntity<?> logout(HttpServletRequest request, @RequestBody UserDto userDto) {
        String res = userService.logout(request, userDto.getEmail());
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/users/unregister/{email}")
    public ResponseEntity<String> unregister(@PathVariable String email) {
        userService.unregister(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원 탈퇴 되었습니다.");
    }

    @PutMapping("/users/edit/{userId}")
    @Operation(summary = "회원 정보 수정", description = "이메일, 비밀번호를 입력받고, 닉네임, 전화번호, 관심사, 프로필 사진을 수정합니다.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<?> editUser(
            @PathVariable("userId") Long userId,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "interests", required = false) String interests,
            @RequestParam(value = "userImg", required = false) MultipartFile userImg) {

        List<String> allowedInterests = Arrays.asList("전자기기", "의류", "가전", "문구", "도서", "신발", "여행용품", "스포츠");

        try {
            List<String> interestsList = interests != null ? Arrays.asList(interests.split(",")) : null;
            if (interestsList != null && interestsList.size() > 3) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관심사는 최대 3개까지 입력할 수 있습니다.");
            }
            if (interestsList != null) {
                for (String interest : interestsList) {
                    if (!allowedInterests.contains(interest)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("관심사는 전자기기, 의류, 가전, 문구, 도서, 신발, 여행용품, 스포츠 중에서 선택해주세요.");
                    }
                }
            }

            EditUserDto editUserDto = EditUserDto.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .phone(phone)
                    .interests(interestsList)
                    .userImg(userImg)
                    .build();

            UserDto updatedUser = userService.editUser(userId, editUserDto);
            return ResponseEntity.ok(UserResponse.of(updatedUser));
        } catch (Exception e) {
            e.printStackTrace(); // 예외 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/users/{userId}")
    @Operation(summary = "유저 아이디로 유저조회", description = "유저 ID에 해당하는 유저의 정보를 조회합니다.")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
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