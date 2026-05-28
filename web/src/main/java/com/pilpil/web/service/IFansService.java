package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.Fans;
import com.pilpil.web.entity.dto.FansVo;

import java.util.List;

/**
 * <p>
 * 关注，粉丝表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-28
 */
public interface IFansService extends IService<Fans> {

    void saveFans(Long targetId);

    void cancelFans(Long targetId);

    boolean IsFans(Long targetId);

    List<FansVo> getFansList(Long userId);

    List<FansVo> getFollowerList(Long userId);

    /**
     * 获取粉丝数
     * @param userId
     * @return
     */
    Integer getFansCount(Long userId);

    /**
     * 获取关注数
     * @param userId
     * @return
     */
    Integer getFollowerCount(Long userId);
}
