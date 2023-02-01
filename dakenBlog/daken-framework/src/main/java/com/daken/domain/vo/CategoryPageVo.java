package com.daken.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPageVo {
    private Long id;
    private String name;

    //描述
    private String description;
    //状态0:正常,1禁用
    private String status;
}
