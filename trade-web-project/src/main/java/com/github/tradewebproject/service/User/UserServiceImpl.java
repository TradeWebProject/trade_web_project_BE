package com.github.tradewebproject.service.User;

import com.github.tradewebproject.controller.UserResponse;
import com.github.tradewebproject.Dto.Jwt.JwtProvider;
import com.github.tradewebproject.Dto.Jwt.Token;
import com.github.tradewebproject.Dto.User.NewUserDto;
import com.github.tradewebproject.Dto.User.Role;
import com.github.tradewebproject.Dto.User.UserDto;

import com.github.tradewebproject.repository.Jwt.TokenRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.util.FileStorageUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service    //비즈니스 로직을 처리하는 service 클래스
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

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
        try{
            UserDto userDto = userRepository.findByEmail(email);

            if(userDto != null) {
                if(encoder.matches(pw, userDto.getUserPassword())) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, pw);
                    List<GrantedAuthority> authoritiesForUser = getAuthoritiesForUser(userDto);

                    Token findToken = tokenRepository.findByUserEmail(email);

                    //JWT 생성
                    Token token = jwtProvider.createToken(authentication, authoritiesForUser);

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
                                .build();

                        log.info("로그인에 성공했습니다.");

                    }
                    return tokenRepository.save(token);
                }
                else{
                    throw new Exception("비밀번호가 일치하지 않습니다.");
                }
            }
            else{
                throw new UserNotFoundException();
            }
        }
        catch (Exception e) {
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

}