package com.chengq.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Map;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * Redis 简易“工具”封装：统一 key/hashKey 用 String，value/hashValue 用 JSON。
 * 对外只暴露常用能力（get/set/del/expire/hash 等），便于业务侧直接调用。
 */
@Service
public class RedisTool {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisTool(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 设置 key-value（不设置过期时间）
     *
     * @param key Redis key
     * @param value 写入的 value（会被 JSON 序列化）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置 key-value，并设置过期时间
     *
     * @param key Redis key
     * @param value 写入的 value（会被 JSON 序列化）
     * @param ttl 过期时长
     */
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 设置 key-value，并设置过期时间（单位：秒）
     *
     * @param key Redis key
     * @param value 写入的 value（会被 JSON 序列化）
     * @param ttlSeconds 过期时长（秒）
     */
    public void set(String key, Object value, long ttlSeconds) {
        set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    /**
     * 设置 key-value（仅当 key 不存在时写入）
     *
     * @param key Redis key
     * @param value 写入的 value（会被 JSON 序列化）
     * @return 写入是否成功（true 表示写入成功）
     */
    public boolean setIfAbsent(String key, Object value) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 设置 key-value（仅当 key 不存在时写入），并设置过期时间
     *
     * @param key Redis key
     * @param value 写入的 value（会被 JSON 序列化）
     * @param ttl 过期时长
     * @return 写入是否成功（true 表示写入成功）
     */
    public boolean setIfAbsent(String key, Object value, Duration ttl) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 获取 key 对应的 value，并反序列化为指定类型
     *
     * @param key Redis key
     * @param clazz 目标类型
     * @return key 不存在时返回 null
     */
    public <T> T get(String key, Class<T> clazz) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object value = ops.get(key);
        if (value == null) return null;
        return objectMapper.convertValue(value, clazz);
    }

    /**
     * 获取 key 对应的值；key 不存在或反序列化失败时返回默认值
     *
     * @param key Redis key
     * @param clazz 目标类型
     * @param defaultValue 默认值
     */
    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        T value = get(key, clazz);
        return value == null ? defaultValue : value;
    }

    /**
     * 判断 key 是否存在
     *
     * @param key Redis key
     * @return true 表示存在，false 表示不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除 key
     *
     * @param key Redis key
     * @return true 表示删除成功（key 存在且被删除）
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 设置过期时间
     *
     * @param key Redis key
     * @param ttl 过期时长
     * @return true 表示设置成功
     */
    public Boolean expire(String key, Duration ttl) {
        return redisTemplate.expire(key, ttl);
    }

    /**
     * 为 key 设置过期时间（单位：秒）
     *
     * @param key Redis key
     * @param ttlSeconds 过期时长（秒）
     * @return true 表示设置成功
     */
    public Boolean expire(String key, long ttlSeconds) {
        return expire(key, Duration.ofSeconds(ttlSeconds));
    }

    /**
     * 数值自增（递增/递减）
     *
     * 注意：Redis 里对应的 value 必须是数值类型，否则操作可能失败/返回 null。
     *
     * @param key Redis key
     * @param delta 增量（可为负数）
     * @return 自增后的新值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 写入 hash：hashKey-field -> value
     *
     * @param key Redis hash key
     * @param hashKey hash 字段名
     * @param value hashValue（会被 JSON 序列化）
     */
    @Deprecated
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 读取 hash：hashKey-field -> value
     *
     * @param key Redis hash key
     * @param hashKey hash 字段名
     * @param clazz 目标类型
     * @return 读取到的值；key/field 不存在返回 null
     */
    @Deprecated
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        Object value = redisTemplate.opsForHash().get(key, hashKey);
        if (value == null) return null;
        return objectMapper.convertValue(value, clazz);
    }





}

