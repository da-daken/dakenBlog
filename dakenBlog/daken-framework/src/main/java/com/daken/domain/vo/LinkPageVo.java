package com.daken.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkPageVo {
    private Long id;

    private String name;

    //审核状态 (0代表审核通过，1代表审核未通过，2代表未审核)
    private String status;

    private String description;
    //网站地址
    private String address;

}