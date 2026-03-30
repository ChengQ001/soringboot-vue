package com.chengq.app.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.chengq.api.entity.BaseEntity;
import com.chengq.api.entity.User;
import com.chengq.app.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * MyBatis Plus配置类
 */
@Configuration
@Slf4j
public class MyBatisPlusConfig {
    
    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 添加乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
    
    /**
     * 获取当前登录用户信息
     * @return 当前登录用户，未登录返回null
     */
    private User getCurrentUser() {
        Object principal = UserContext.getAuthentication() != null ? UserContext.getAuthentication().getPrincipal() : null;
        if (principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    /**
     * 自动填充处理器
     * 用于自动填充创建时间、更新时间、创建人、更新人等字段
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            
            @Override
            public void insertFill(MetaObject metaObject) {
                log.debug("开始自动填充insert操作的字段");
                
                // 判断是否是BaseEntity的子类
                if (metaObject.getOriginalObject() instanceof BaseEntity) {
                    BaseEntity entity = (BaseEntity) metaObject.getOriginalObject();
                    
                    // 自动填充创建时间
                    this.strictInsertFill(metaObject, "createdAt", Date.class, new Date());
                    
                    // 自动填充更新时间
                    this.strictInsertFill(metaObject, "updatedAt", Date.class, new Date());
                    
                    // 获取当前登录用户信息
                    User currentUser = getCurrentUser();
                    if (currentUser != null) {
                        // 使用当前登录用户信息填充创建人ID和名称
                        this.strictInsertFill(metaObject, "createdBy", Long.class, currentUser.getId());
                        this.strictInsertFill(metaObject, "createdByName", String.class, currentUser.getUsername());
                        
                        // 使用当前登录用户信息填充更新人ID和名称
                        this.strictInsertFill(metaObject, "updatedBy", Long.class, currentUser.getId());
                        this.strictInsertFill(metaObject, "updatedByName", String.class, currentUser.getUsername());
                    } else {
                        // 未登录时使用默认值
                        this.strictInsertFill(metaObject, "createdBy", Long.class, 0L);
                        this.strictInsertFill(metaObject, "createdByName", String.class, "system");
                        this.strictInsertFill(metaObject, "updatedBy", Long.class, 0L);
                        this.strictInsertFill(metaObject, "updatedByName", String.class, "system");
                    }
                    
                    // 默认值：版本号
                    this.strictInsertFill(metaObject, "version", Integer.class, 1);
                    
                    // 默认值：是否删除（0-未删除，1-已删除）
                    this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
                }
            }
            
            @Override
            public void updateFill(MetaObject metaObject) {
                log.debug("开始自动填充update操作的字段");
                
                // 判断是否是BaseEntity的子类
                if (metaObject.getOriginalObject() instanceof BaseEntity) {
                    // 自动填充更新时间
                    this.strictUpdateFill(metaObject, "updatedAt", Date.class, new Date());
                    
                    // 获取当前登录用户信息
                    User currentUser = getCurrentUser();
                    if (currentUser != null) {
                        // 使用当前登录用户信息填充更新人ID和名称
                        this.strictUpdateFill(metaObject, "updatedBy", Long.class, currentUser.getId());
                        this.strictUpdateFill(metaObject, "updatedByName", String.class, currentUser.getUsername());
                    } else {
                        // 未登录时使用默认值
                        this.strictUpdateFill(metaObject, "updatedBy", Long.class, 0L);
                        this.strictUpdateFill(metaObject, "updatedByName", String.class, "system");
                    }
                }
            }
        };
    }
}
