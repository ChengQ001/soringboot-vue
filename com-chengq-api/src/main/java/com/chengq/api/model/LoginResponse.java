package com.chengq.api.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功返回体：用户标识、JWT、角色与可切换园区等，供前端落库与请求头 {@code X-Park-Id} 使用。
 */
@Data
@NoArgsConstructor
public class LoginResponse {
    /** 用户主键（tb_user.id） */
    private Long id;
    /** 访问令牌（JWT），后续请求置于 Authorization 等头中 */
    private String token;
    /** 登录用户名（展示用，与 tb_user.username 一致） */
    private String username;
    /** 登录手机号，与 LoginRequest.phone 一致 */
    private String phone;
    /** 兼容旧前端：主展示角色名（如 ADMIN、USER） */
    private String role;
    /** 当前用户全部角色名 */
    private List<String> roles = new ArrayList<>();
    /** 当前用户全部角色主键（tb_role.id），与 roles 一一对应（可能含 null） */
    private List<Long> roleIds = new ArrayList<>();
    /** 可切换的园区列表 */
    private List<ParkLoginItem> parks = new ArrayList<>();
    /** 默认选中的园区 ID（与请求头 X-Park-Id 对齐） */
    private Long defaultParkId;

    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
