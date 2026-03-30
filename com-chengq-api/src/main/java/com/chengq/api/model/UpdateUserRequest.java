package com.chengq.api.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "用户ID不能为空")
    private Long id;
    
    private String username;
    
    private String password;
    
    private String role;
}
