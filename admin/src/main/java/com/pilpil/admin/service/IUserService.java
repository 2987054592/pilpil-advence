package com.pilpil.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.admin.entity.dto.UserDto;
import com.pilpil.admin.entity.vo.QueryUser;
import com.pilpil.common.entity.po.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>
 * 用户信息 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-20
 */
public interface IUserService extends IService<User> {

    boolean login(UserDto userDto, HttpServletResponse response);

    QueryUser QueryPageUser(Integer pageNum, Integer pageSize);
}
