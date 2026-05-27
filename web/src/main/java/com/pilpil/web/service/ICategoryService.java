package com.pilpil.web.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pilpil.common.entity.po.Category;
import com.pilpil.common.entity.vo.CategoryVo;


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
    List<CategoryVo> getCategory();

}
