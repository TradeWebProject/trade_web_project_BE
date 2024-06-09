package com.github.tradewebproject.domain;


import com.github.tradewebproject.Dto.User.UserDto;
import io.swagger.v3.oas.annotations.info.Contact;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "user_password", nullable = false, length = 255)
    private String userPassword;

    @Column(name = "user_nickname", nullable = false, length = 255)
    private String userNickname;

    @Column(name = "user_phone", nullable = false, length = 255)
    private String userPhone;

    @Column(name = "user_interests", columnDefinition = "TEXT")
    private String userInterests;

    @Column(name = "user_img", length = 255)
    private String userImg;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public static User from(UserDto userDto){
        return User.builder()
                .email(userDto.getEmail())
                .userPassword(userDto.getUserPassword())
                .userNickname(userDto.getUserNickname())
                .userPhone(userDto.getUserPhone())
                .userInterests(userDto.getUserInterests())
                .userImg(userDto.getUserImg())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public UserDto toDTO(){
        return UserDto.builder()
                .email(this.email)
                .userPassword(this.userPassword)
                .userNickname(this.userNickname)
                .userPhone(this.userPhone)
                .userImg(this.userImg)
                .build();
    }
}

