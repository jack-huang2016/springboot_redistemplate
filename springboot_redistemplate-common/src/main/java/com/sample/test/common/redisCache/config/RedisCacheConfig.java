/**
 * FileName: RedisConfig
 * Author:   huang.yj
 * Date:     2019/11/25 19:07
 * Description: redis配置类
 */
package com.sample.test.common.redisCache.config;

import com.sample.test.common.redisCache.serializer.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 〈redis配置类〉
 *
 * @author huang.yj
 * @create 2019/11/25
 * @since 0.0.1
 */
@Slf4j
@Configuration
public class RedisCacheConfig {
    /**
     * springboot2.0版本以上默认使用LettuceConnectionFactory
     */
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 虽然springboot整合redis的RedisAutoConfiguration类已经定义了redisTemplate的bean，但是因为默认使用JdkSerializationRedisSerializer序列化，
     * 所以需要重新定义redisTemplate，配置序列化规则，覆盖默认的配置
     * @return RedisTemplate
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);

        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用StringRedisSerializer
        template.setHashKeySerializer(stringRedisSerializer);

        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        // hash的value序列化也采用fastJsonRedisSerializer
        template.setHashValueSerializer(fastJsonRedisSerializer);

        // 开启事务
        //template.setEnableTransactionSupport(true);
        template.setDefaultSerializer(fastJsonRedisSerializer);
        template.afterPropertiesSet();

        log.debug("自定义RedisTemplate加载完成");
        return template;
    }
}