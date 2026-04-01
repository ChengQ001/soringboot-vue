package com.chengq.app.exception;

/**
 * 业务状态码约定（响应体 {@code ApiResponse.code}），可与 HTTP 语义对齐，也可扩展模块内分段码（如 400101）。
 */
public final class BizCodes {

    private BizCodes() {}

    /** 参数错误、业务规则不满足 */
    public static final int BAD_REQUEST = 400;

    /** 未登录、凭证错误（与鉴权失败语义一致，由前端视情况处理） */
    public static final int UNAUTHORIZED = 401;

    /** 无权限、系统保留资源禁止操作 */
    public static final int FORBIDDEN = 403;

    /** 资源不存在 */
    public static final int NOT_FOUND = 404;

    /** 资源冲突：重复、已占用 */
    public static final int CONFLICT = 409;

    /** 服务器或持久层异常（包装后的失败） */
    public static final int INTERNAL_ERROR = 500;
}
