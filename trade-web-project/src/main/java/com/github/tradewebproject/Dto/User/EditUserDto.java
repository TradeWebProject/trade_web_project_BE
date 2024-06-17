package com.github.tradewebproject.Dto.User;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class EditUserDto {
    private String email;
    private String password;
    private String nickname;
    private String phone;
    private List<String> interests;
    private MultipartFile userImg;
}