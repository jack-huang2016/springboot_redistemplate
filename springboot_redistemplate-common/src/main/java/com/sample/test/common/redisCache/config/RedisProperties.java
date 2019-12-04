/**
 * FileName: RedisProperties
 * Author:   huang.yj
 * Date:     2019/11/29 11:11
 * Description: redis配置属性类
 */
package com.sample.test.common.redisCache.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 〈redis配置属性类〉
 *
 * @author huang.yj
 * @create 2019/11/29
 * @since 0.0.1
 */
@Data
@Component
public class RedisProperties {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    /* application-redis.yml没有配置密码，所以注释掉该字段，不然会报错
    @Value("${spring.redis.password}")
    private String password;*/

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWait;
}