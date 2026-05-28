package com.pilpil.admin.service.impl;


import com.pilpil.admin.mapper.FansMapper;
import com.pilpil.admin.service.IFansService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.common.entity.po.Fans;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 关注，粉丝表 服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-28
 */
@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans> implements IFansService {

}
