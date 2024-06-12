package com.github.tradewebproject.Dto.User;

import io.swagger.v3.oas.annotations.info.Contact;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    private Long userId;
    private String email;
    private String userPassword;
    private String userNickname;
    private String userPhone;
    private List<String> userInterests;
    private String userImg;
    private final Role role = Role.USER;
    private final Deleted deleted = Deleted.INUSE;

}