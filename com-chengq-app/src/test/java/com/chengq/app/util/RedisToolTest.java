package com.chengq.app.util;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.chengq.app.Application;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class RedisToolTest {

    @Autowired
    private RedisTool redisTool;

    private String keyPrefix = "redisToolTest:" + UUID.randomUUID();

    @AfterEach
    void cleanup() {
        // best-effort 清理
//        redisTool.delete(keyPrefix + ":str");
//        redisTool.delete(keyPrefix + ":inc");
//        redisTool.delete(keyPrefix + ":expire");
//        redisTool.delete(keyPrefix + ":hash");
    }

    @Test
    void testSetGetDeleteExpireIncrementHash() throws Exception {
        // set/get
        String strKey = keyPrefix + ":str";
        redisTool.set(strKey, "hello");
        Boolean b = redisTool.hasKey(strKey);
        String s = redisTool.get(strKey, String.class);


        // increment（从 0 开始）
        String incKey = keyPrefix + ":inc";
        redisTool.set(incKey, 0);
        Long incVal = redisTool.increment(incKey, 1);
        assertNotNull(incVal);
        assertEquals(1L, incVal);

        // expire
        String expireKey = keyPrefix + ":expire";
        redisTool.set(expireKey, "tmp", Duration.ofMillis(500));
        assertTrue(redisTool.hasKey(expireKey));
        Thread.sleep(650);
        assertFalse(redisTool.hasKey(expireKey));

        // hash hset/hget/hgetAll
        String hashKey = keyPrefix + ":hash";
        String field = "field1";
        redisTool.hSet(hashKey, field, "v1");
        assertEquals("v1", redisTool.hGet(hashKey, field, String.class));



    }
}

