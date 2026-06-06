package com.pilpil.web.entity.dto;

import com.pilpil.common.enums.SexType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateDto {
    private Integer id;
    private String nickName;
    private String email;
    private String password;
    private String avatar;
    private String introduce;
    private SexType sex;
    private String background;
}
