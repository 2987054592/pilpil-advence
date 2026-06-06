package com.pilpil.admin.controller;


import com.pilpil.admin.entity.dto.UserDto;
import com.pilpil.admin.entity.vo.QueryUser;
import com.pilpil.admin.service.IUserService;
import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.po.User;
import com.pilpil.common.enums.StatusType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public Result<QueryUser> QueryPageUser(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return Result.success(userService.QueryPageUser(pageNum,pageSize));
    }
    @PostMapping
    public Result updateUser(@RequestParam Integer id, @RequestParam StatusType statusType){
        userService.lambdaUpdate().eq(User::getId,id).set(User::getStatus,statusType).update();
        return Result.success();
    }
}
