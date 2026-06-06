package com.pilpil.web.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pilpil.common.entity.po.FavoriteVideo;
import org.apache.ibatis.annotations.Delete;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2026-05-27
 */
public interface FavoriteVideoMapper extends BaseMapper<FavoriteVideo> {


    @Delete("delete from favorite_video where favorite_id = #{id}")
    void deleteByFavoriteId(Integer id);
}
