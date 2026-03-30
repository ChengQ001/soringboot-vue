package com.chengq.app.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XXL-Job executor Spring 集成配置。
 *
 * <p>该配置会启动 xxl-job executor 的内置 Netty 通信服务，用于接收 admin 的调度。</p>
 */
@Configuration
public class XxlJobConfig {

    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;

    @Value("${xxl.job.accessToken:}")
    private String accessToken;

    @Value("${xxl.job.executor.appname:com-chengq-executor}")
    private String appname;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port:0}")
    private int port;

    @Value("${xxl.job.executor.logpath:./logs/xxl-job/jobhandler}")
    private String logpath;

    @Value("${xxl.job.executor.logretentiondays:30}")
    private int logretentiondays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAccessToken(accessToken);
        executor.setAppname(appname);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setLogPath(logpath);
        executor.setLogRetentionDays(logretentiondays);
        return executor;
    }
}

