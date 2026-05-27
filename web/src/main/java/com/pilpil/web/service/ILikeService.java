package com.pilpil.web.service;

import com.pilpil.common.entity.po.Like;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.enums.LikeBisType;
import com.pilpil.web.entity.dto.likeDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 点赞表 服务类
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
public interface ILikeService extends IService<Like> {

    void savelike(likeDto likeDto);

    Map<LikeBisType, List<Integer>> getlike(Integer videoId);
}
