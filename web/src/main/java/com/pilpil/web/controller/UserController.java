package com.pilpil.web.controller;


import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.vo.UserVoDetail;
import com.pilpil.web.entity.dto.UserDto;

import com.pilpil.common.entity.vo.UserVo;
import com.pilpil.web.service.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-20
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final IUserService userService;
    @PostMapping
    public Result saveUser(@RequestBody UserDto userDto){
        userService.saveUser(userDto);
        return Result.success();
    }
    @GetMapping("/code")
    public Result code(String email){
        return Result.success(userService.code(email));
    }
    @PostMapping("/login")
    public Result<UserVo> login(@RequestBody UserDto userDto, HttpServletResponse response){
        UserVo user=userService.login(userDto,response);
        return Result.success(user);
    }
    @GetMapping("/exit")
    public Result exit(HttpServletRequest request){
        userService.exit(request);
        return Result.success();
    }
    @GetMapping("/me")
    public Result<UserVoDetail> me() {
        return Result.success(userService.me());
    }

    @GetMapping("/info")
    public Result<UserVoDetail> info(String name){
        return Result.success(userService.info(name));
    }
    @GetMapping("/info/simple")
    public Result<UserVo> infoSimple(String name){
        return Result.success(userService.infoSimple(name));
    }
}
