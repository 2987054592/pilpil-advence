package com.pilpil.web.service.impl;

import cn.hutool.json.JSONUtil;
import com.pilpil.common.constants.redis.redisContanst;
import com.pilpil.common.entity.po.Category;
import com.pilpil.common.entity.vo.CategoryVo;
import com.pilpil.web.mapper.CategoryMapper;
import com.pilpil.web.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2026-05-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    private final StringRedisTemplate redisTemplate;
    @Override
    public List<CategoryVo> getCategory() {
        String json = redisTemplate.opsForValue().get(redisContanst.Category.CATEGORY_LIST);
        if(json!=null && !json.isEmpty()){
            return JSONUtil.toList(json, CategoryVo.class);
        }

        List<Category> list = lambdaQuery().orderByAsc(Category::getSort).list();
        if(list.isEmpty()){
            return Collections.emptyList();
        }
        List<CategoryVo> categoryVos = list.stream().map(CategoryVo::new).toList();
        List<CategoryVo> root = categoryVos.stream()
                .filter(vo -> vo.getParentId() == 0)
                .toList();
        for(CategoryVo vo:root){
            vo.setChildren(
                    categoryVos.stream()
                            .filter(vo1 -> vo1.getParentId().equals(vo.getId()))
                            .toList()
            );
        }
        redisTemplate.opsForValue().set(redisContanst.Category.CATEGORY_LIST, JSONUtil.toJsonStr(root));
        return root;
    }
}
