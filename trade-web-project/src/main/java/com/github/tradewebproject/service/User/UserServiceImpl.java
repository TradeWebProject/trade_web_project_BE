package com.github.tradewebproject.service.User;

import com.github.tradewebproject.Dto.User.getUserDto;
import com.github.tradewebproject.controller.UserResponse;
import com.github.tradewebproject.Dto.Jwt.JwtProvider;
import com.github.tradewebproject.Dto.Jwt.Token;
import com.github.tradewebproject.Dto.User.NewUserDto;
import com.github.tradewebproject.Dto.User.Role;
import com.github.tradewebproject.Dto.User.UserDto;

import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Jwt.TokenJpaRepository;
import com.github.tradewebproject.repository.Jwt.TokenRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.util.FileStorageUtil;
import com.github.tradewebproject.util.MultipartFileResource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RequiredArgsConstructor
@Service    //비즈니스 로직을 처리하는 service 클래스
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserJpaRepository userJpaRepository;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;
    private final TokenJpaRepository tokenJpaRepository;

    @Value("${UPLOAD_DIR}")
    private String uploadDir;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Override
    public String encodePassword(String password) { //패스워드 암호화
        return encoder.encode(password);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) { //암호화된 패스워드와 입력한 패스워드가 일치여부 체크
        return encoder.matches(rawPassword, encodedPassword);
    }

    // 회원가입
    @Override
    @Transactional
    public UserDto register(NewUserDto userDto) throws IOException {

        //email 중복 검사
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new RuntimeException("이미 등록된 이메일 주소입니다.");
        }

        String password = encodePassword(userDto.getUserPassword());

        String imagePath = null;
        if (userDto.getUserImg() != null && !userDto.getUserImg().isEmpty()) {
            imagePath = fileStorageUtil.storeFile(userDto.getUserImg());
        }


        UserDto newUser = UserDto.builder()
                .email(userDto.getEmail())
                .userPassword(password)
                .userNickname(userDto.getUserNickname())
                .userPhone(userDto.getUserPhone())
                .userInterests(userDto.getUserInterests())
                .userImg(imagePath)
                .build();

        log.info("회원가입에 성공했습니다.");

        return userRepository.save(newUser);
    }

    @Override
    public Token login(String email, String pw) throws Exception {
        try {
            UserDto userDto = userRepository.findByEmail(email);

            if (userDto != null) {
                if (encoder.matches(pw, userDto.getUserPassword())) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, pw);
                    List<GrantedAuthority> authoritiesForUser = getAuthoritiesForUser(userDto);

                    Token findToken = tokenRepository.findByUserEmail(email);

                    // JWT 생성
                    Token token = jwtProvider.createToken(authentication, authoritiesForUser, userDto.getUserId());

                    if (findToken == null) {
                        log.info("발급한 토큰이 없습니다. 새로운 토큰을 발급합니다.");
                    } else {
                        log.info("이미 발급된 토큰이 있습니다. 토큰을 업데이트합니다.");
                        token = Token.builder()
                                .id(findToken.getId())
                                .grantType(token.getGrantType())
                                .accessToken(token.getAccessToken())
                                .accessTokenTime(token.getAccessTokenTime())
                                .refreshToken(token.getRefreshToken())
                                .refreshTokenTime(token.getRefreshTokenTime())
                                .userEmail(token.getUserEmail())
                                .userId(userDto.getUserId())  // 여기에서 userId를 설정합니다.
                                .build();
                    }
                    log.info("로그인에 성공했습니다.");
                    return tokenRepository.save(token);
                } else {
                    throw new Exception("비밀번호가 일치하지 않습니다.");
                }
            } else {
                throw new UserNotFoundException();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public String logout(HttpServletRequest request, String email){
        try{
            Optional<String> refreshToken = jwtProvider.extractRefreshToken(request);
            Optional<String> accessToken = jwtProvider.extractAccessToken(request);

            Token findToken = tokenRepository.findByUserEmail(email);
            tokenRepository.deleteById(findToken.getId());

            return "로그아웃에 성공하였습니다.";
        }
        catch (Exception e){
            return "로그아웃 시 토큰 초기화에 실패하였습니다.";
        }
    }

    @Override
    public void unregister(String email) {
        UserDto userDto = userRepository.findByEmail(email);
        if (userDto != null) {
            userRepository.deleteByEmail(email);
            log.info("회원 탈퇴 되었습니다.");
        } else {
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다. " + email);
        }
    }

    @Override
    public UserResponse getByEmail(String email) {
        UserDto findMember = userRepository.findByEmail(email);
        if(findMember != null)
            return UserResponse.of(findMember);
        else
            throw new UserNotFoundException();
    }

    private List<GrantedAuthority> getAuthoritiesForUser(UserDto userDto) {
        Role memberRole = userDto.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole.name()));
        log.info("role : " + authorities);
        return authorities;
    }



//    @Override base64 인코딩한거
//    public getUserDto getUserById(Long userId) {
//        User userEntity = userJpaRepository.findByUserId(userId);
//        if (userEntity == null) {
//            return null;
//        }
//
//        String base64Image = ""; // base64 이미지를 저장할 변수 초기화
//        try {
//            Path filePath = Paths.get(uploadDir).resolve(userEntity.getUserImg()).normalize(); // 이미지 파일 경로
//            byte[] imageBytes = Files.readAllBytes(filePath); // 이미지 파일 읽기
//            base64Image = Base64.getEncoder().encodeToString(imageBytes); // base64로 인코딩
//        } catch (IOException e) {
//            e.printStackTrace(); // 예외 처리
//            // 로그 남기기나 적절한 예외 처리 로직 추가
//        }
//
//        return getUserDto.builder()
//                .email(userEntity.getEmail())
//                .user_nickname(userEntity.getUserNickname())
//                .user_phone(userEntity.getUserPhone())
//                .userInterests(userEntity.getUserInterests())
//                .user_img(base64Image) // base64 인코딩된 이미지 설정
//
//                .build();
//    }

    //    @Override // 이미지 자체담은 코드
//    public ResponseEntity<?> getUserById(Long userId) {
//        User userEntity = userJpaRepository.findByUserId(userId);
//        if (userEntity == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // getUserDto 생성
//        getUserDto getuserDto = getUserDto.builder()
//                .email(userEntity.getEmail())
//                .user_nickname(userEntity.getUserNickname())
//                .user_phone(userEntity.getUserPhone())
//                .userInterests(userEntity.getUserInterests())
//                .build();
//
//        // 이미지 파일 읽기
//        try {
//            Path filePath = Paths.get(uploadDir).resolve(userEntity.getUserImg()).normalize();
//            byte[] imageBytes = Files.readAllBytes(filePath);
//
//            // 멀티파트 응답 생성
//            MultipartFileResource multipartFileResource = new MultipartFileResource(imageBytes, userEntity.getUserImg());
//
//            // 멀티파트 응답을 위한 HttpHeaders 설정
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setContentDispositionFormData("attachment", userEntity.getUserImg());
//            headers.setContentLength(imageBytes.length);
//
//            // 멀티파트 응답 생성
//            MultiValueMap<String, Object> multipartResponse = new LinkedMultiValueMap<>();
//            multipartResponse.add("user", getuserDto);
//            multipartResponse.add("file", new HttpEntity<>(multipartFileResource, headers));
//
//            return ResponseEntity.ok(multipartResponse);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
    @Override
    public ResponseEntity<?> getUserById(Long userId) {
        User userEntity = userJpaRepository.findByUserId(userId);
        if (userEntity == null) {
            return ResponseEntity.notFound().build();
        }

        String baseImageUrl = "/images/";
        // getUserDto 생성
        getUserDto getuserDto = getUserDto.builder()
                .email(userEntity.getEmail())
                .user_nickname(userEntity.getUserNickname())
                .user_phone(userEntity.getUserPhone())
                .userInterests(userEntity.getUserInterests())
                .user_img(baseImageUrl + userEntity.getUserImg())
                .build();

        // JSON 응답을 위한 HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok()
                .headers(headers)
                .body(getuserDto);
    }

}