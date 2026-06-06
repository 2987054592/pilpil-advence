package com.pilpil.admin.entity.vo;

import com.pilpil.common.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryUser {
    private Integer totalPage;
    private Integer TotalCount;
    private List<User> userList;
}
