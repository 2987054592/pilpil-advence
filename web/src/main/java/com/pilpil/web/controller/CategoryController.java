package com.pilpil.web.controller;



import com.pilpil.common.entity.Result;

import com.pilpil.common.entity.vo.CategoryVo;
import com.pilpil.web.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;
    @GetMapping
    public Result<List<CategoryVo>> getCategory(){
        return Result.success(categoryService.getCategory());
    }
}
