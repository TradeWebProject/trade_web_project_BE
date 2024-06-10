package com.github.tradewebproject.Dto.User;

import io.swagger.v3.oas.annotations.info.Contact;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String email;
    private String userPassword;
    private String userNickname;
    private String userPhone;
    private String userInterests;
    private String userImg;
    private final Role role = Role.USER;
    private final Deleted deleted = Deleted.INUSE;

}
