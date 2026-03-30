package com.chengq.app.util;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * Redisson 分布式锁封装（建议使用 withLock，自动 unlock）。
 */
@Service
public class RedissonLockTool {

    private final RedissonClient redissonClient;

    public RedissonLockTool(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取锁对象（不直接加锁）
     *
     * @param lockKey 锁 key
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 不带等待/租约参数的加锁：直接调用 RLock#lock()
     * <p>
     * 这种方式由 Redisson 的 watchdog 负责“续租/自动解锁”，你无需手动传 leaseTime。
     * </p>
     *
     * @param lockKey 锁 key
     */
    public void lock(String lockKey) {
        Objects.requireNonNull(lockKey, "lockKey");
        getLock(lockKey).lock();
    }

    /**
     * 无等待/租约参数的 withLock：内部直接 lock() + finally unlock()
     *
     * @param lockKey 锁 key
     * @param action 临界区逻辑
     */
    public void withLock(String lockKey, Runnable action) {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(lockKey, "lockKey");

        RLock lock = getLock(lockKey);
        lock.lock();
        try {
            action.run();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 无等待/租约参数的 withLock（返回值版）：内部直接 lock() + finally unlock()
     *
     * @param lockKey 锁 key
     * @param action 临界区逻辑
     * @param <T> 返回类型
     * @return action 的返回值
     */
    public <T> T withLock(String lockKey, Callable<T> action) throws Exception {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(lockKey, "lockKey");

        RLock lock = getLock(lockKey);
        lock.lock();
        try {
            return action.call();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 无等待/租约参数的 withLock（Supplier 版）：内部直接 lock() + finally unlock()
     *
     * @param lockKey 锁 key
     * @param action 临界区逻辑
     * @param <T> 返回类型
     * @return action 的返回值
     */
    public <T> T withLock(String lockKey, Supplier<T> action) {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(lockKey, "lockKey");

        RLock lock = getLock(lockKey);
        lock.lock();
        try {
            return action.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 尝试获取锁：在 waitTime 内等待，leaseTime 作为租约时间（自动续约/释放由 Redisson 配置决定）
     *
     * @param lockKey 锁 key
     * @param waitTime 等待时间（最多等待多久）
     * @param leaseTime 租约时间（持有多久后自动释放）
     * @return 成功返回 true；失败返回 false
     */
    public boolean tryLock(String lockKey, Duration waitTime, Duration leaseTime) {
        Objects.requireNonNull(lockKey, "lockKey");
        Objects.requireNonNull(waitTime, "waitTime");
        Objects.requireNonNull(leaseTime, "leaseTime");

        RLock lock = getLock(lockKey);
        try {
            return lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁（仅释放“当前线程已持有”的锁）
     *
     * @param lockKey 锁 key
     */
    public void unlock(String lockKey) {
        if (lockKey == null) return;
        RLock lock = getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * withLock：自动 tryLock + finally unlock
     */
    public void withLock(String lockKey, Duration waitTime, Duration leaseTime, Runnable action) {
        Objects.requireNonNull(action, "action");

        boolean locked = tryLock(lockKey, waitTime, leaseTime);
        if (!locked) {
            throw new IllegalStateException("获取分布式锁失败：lockKey=" + lockKey);
        }

        try {
            action.run();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * withLock：自动 tryLock + finally unlock（返回值版）
     */
    public <T> T withLock(String lockKey, Duration waitTime, Duration leaseTime, Callable<T> action) throws Exception {
        Objects.requireNonNull(action, "action");

        boolean locked = tryLock(lockKey, waitTime, leaseTime);
        if (!locked) {
            throw new IllegalStateException("获取分布式锁失败：lockKey=" + lockKey);
        }

        try {
            return action.call();
        } finally {
            unlock(lockKey);
        }
    }

    /**
     * withLock：自动 tryLock + finally unlock（Supplier 版）
     */
    public <T> T withLock(String lockKey, Duration waitTime, Duration leaseTime, Supplier<T> action) {
        Objects.requireNonNull(action, "action");
        boolean locked = tryLock(lockKey, waitTime, leaseTime);
        if (!locked) {
            throw new IllegalStateException("获取分布式锁失败：lockKey=" + lockKey);
        }

        try {
            return action.get();
        } finally {
            unlock(lockKey);
        }
    }
}

