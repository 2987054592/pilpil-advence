package com.pilpil.admin.controller;


import com.pilpil.admin.entity.dto.UserDto;
import com.pilpil.admin.service.IUserService;
import com.pilpil.common.entity.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.pilpil.common.constants.Exception.exceptionConstants.User.USER_LOGIN_ERROR;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-20
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto, HttpServletResponse response){
        boolean login = userService.login(userDto,response);
        return login ? Result.success() : Result.error(USER_LOGIN_ERROR);
    }
}
