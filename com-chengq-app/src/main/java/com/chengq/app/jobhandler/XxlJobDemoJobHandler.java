package com.chengq.app.jobhandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * XXL-Job 示例任务处理器。
 *
 * <p>在 xxl-job-admin 中创建任务时，handler 名称请填：{@code demoJobHandler}</p>
 */
@Slf4j
@Component
public class XxlJobDemoJobHandler {

    @XxlJob("demoJobHandler")
    public void execute() {
        log.info("XXL-Job执行demoJobHandler成功");
        // 显式标记执行结果，避免框架判定 handle result 丢失为失败
        XxlJobHelper.handleSuccess("ok");
    }
}

