package com.chengq.api.model;

import lombok.Data;

@Data
public class SysParkUpdateRequest {
    private Long id;
    private String name;
    private String description;
}

