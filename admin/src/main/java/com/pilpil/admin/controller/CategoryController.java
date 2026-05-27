package com.pilpil.admin.controller;



import com.pilpil.admin.entity.dto.CategoryDto;
import com.pilpil.admin.service.ICategoryService;

import com.pilpil.common.entity.Result;
import com.pilpil.common.entity.po.Category;
import com.pilpil.common.entity.vo.CategoryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2026-05-21
 */
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;
    @PostMapping
    public Result saveCategory(@RequestBody CategoryDto category){
        categoryService.saveCategory(category);
        return Result.success();
    }
    @GetMapping
    public Result<List<CategoryVo>> getCategory(){
        return Result.success(categoryService.getCategory());
    }
    @PostMapping("/update")
    public Result updateCategory(@RequestBody Category category){
        categoryService.updateCategory(category);
        return Result.success();
    }
    @DeleteMapping
    public Result deleteCategory(Integer id){
        categoryService.removeCategory(id);
        return Result.success();
    }
}
