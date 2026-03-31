package com.chengq.api.model;

import lombok.Data;

import java.util.Date;

@Data
public class SysParkVO {
    private Long id;
    private String name;
    private String description;
    private Date createdAt;
    private Date updatedAt;
}

