package com.pilpil.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.admin.entity.dto.CategoryDto;
import com.pilpil.comment.entity.po.Category;
import com.pilpil.comment.entity.vo.CategoryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2026-05-21
 */
public interface ICategoryService extends IService<Category> {

    void saveCategory(CategoryDto category);

    List<CategoryVo> getCategory();

    void updateCategory(Category category);

    void removeCategory(Integer id);
}
