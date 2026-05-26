package com.pilpil.web.service;

import com.pilpil.comment.entity.po.User;
import com.pilpil.comment.entity.vo.UserVoDetail;
import com.pilpil.web.entity.dto.UserDto;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.comment.entity.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-20
 */
public interface IUserService extends IService<User> {

    void saveUser(UserDto userDto);

    String code(String email);

    UserVo login(UserDto userDto, HttpServletResponse response);

    void exit(HttpServletRequest request);

    UserVoDetail me();
}
