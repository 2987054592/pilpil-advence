package com.pilpil.admin.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.pilpil.admin.entity.dto.CategoryDto;
import com.pilpil.admin.mapper.CategoryMapper;
import com.pilpil.admin.service.ICategoryService;
import com.pilpil.common.constants.redis.redisContanst;
import com.pilpil.common.entity.po.Category;
import com.pilpil.common.entity.vo.CategoryVo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pilpil.common.exception.illegalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pilpil.common.constants.Exception.exceptionConstants.Category.*;


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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
    private final StringRedisTemplate redisTemplate;
    @Override
    public void saveCategory(CategoryDto category) {
        Category one = lambdaQuery().eq(Category::getName, category.getName()).one();
        if(one!=null){
            throw new illegalException(CATEGORY_EXIST);
        }
        Category parent = lambdaQuery().eq(Category::getId, category.getPId()).one();
        if(parent!=null){
            if(parent.getPId()!=0){
                throw new illegalException(CATEGORY_PARENT_EMPTY);
            }
        }
        save(BeanUtil.toBean(category, Category.class));
        List<CategoryVo> newDate = getCategory();
        String json = JSONUtil.toJsonStr(newDate);
        redisTemplate.opsForValue().set(redisContanst.Category.CATEGORY_LIST, json);
        redisTemplate.expire(redisContanst.Category.CATEGORY_LIST, 1L, TimeUnit.DAYS);
        redisTemplate.delete(redisContanst.Category.CATEGORY_LIST);
    }
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
        return root;
    }

    @Override
    public void updateCategory(Category category) {
        Category one1 = lambdaQuery().eq(Category::getName, category.getName())
                .ne(Category::getId, category.getId())
                .one();
        if(one1!=null){
            throw new illegalException(CATEGORY_EXIST);
        }
        Integer pId = category.getPId();
        if(pId!=0){
            Category one = lambdaQuery().eq(Category::getId, pId).one();
            if(one==null){
                throw new illegalException(CATEGORY_NOT_EXIST);
            }
        }
        updateById(category);
       redisTemplate.delete(redisContanst.Category.CATEGORY_LIST);
    }

    @Override
    public void removeCategory(Integer id) {
        Category category = lambdaQuery().eq(Category::getId, id).one();
        List<Category> list = lambdaQuery().eq(Category::getPId, category.getId()).list();
        if(!list.isEmpty()) {
            removeByIds(list);
        }
        removeById(category);
        redisTemplate.delete(redisContanst.Category.CATEGORY_LIST);
    }
}
