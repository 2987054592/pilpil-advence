package com.pilpil.web.service;

import com.pilpil.common.entity.po.User;
import com.pilpil.common.entity.vo.ExperienceVo;
import com.pilpil.common.entity.vo.UserVoDetail;
import com.pilpil.web.entity.dto.UserDto;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.vo.UserVo;
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

    UserVo login(UserDto userDto, HttpServletResponse response,HttpServletRequest request);

    void exit(HttpServletRequest request);

    UserVoDetail me();

    UserVoDetail info(String name);

    UserVo infoSimple(String name);

    void ExperienceExchange(ExperienceVo experienceVo);
}
