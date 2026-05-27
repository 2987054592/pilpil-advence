package com.pilpil.common.entity.vo;


import com.pilpil.common.entity.po.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CategoryVo {
    private Integer id;
    private String name;
    private Integer parentId;
    private List<CategoryVo> children;
    public CategoryVo(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.parentId = category.getPId();
    }
}
