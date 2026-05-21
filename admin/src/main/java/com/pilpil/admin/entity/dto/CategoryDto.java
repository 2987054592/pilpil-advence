package com.pilpil.admin.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    /**
     * 父类id，0为主分类
     */
    private Integer pId;

    /**
     * 分类的名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;
}
