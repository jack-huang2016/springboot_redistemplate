/**
 * FileName: RedisTestController
 * Author:   huang.yj
 * Date:     2019/11/27 10:29
 * Description: redis测试控制器
 */
package com.sample.test.web.controller;

import com.sample.test.common.entity.User;
import com.sample.test.common.redisCache.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 〈redis测试控制器〉
 * 一般redisUtil、redisTemplate、stringRedisTemplate等类的操作都是在dao层操作的，
 * 在此仅仅为了保证功能正常，测试方便。
 *
 * @author huang.yj
 * @create 2019/11/27
 * @since 0.0.1
 */
@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     *@描述  设置redis以及缓存的过期时间
     *@参数  []
     *@返回值  org.springframework.http.ResponseEntity<java.lang.String>
     *@创建人  huang.yj
     *@创建时间  2019/11/27
     */
    @GetMapping(value = "test")
    public ResponseEntity<String> test(){
        try {
            // 缓存设置10秒超时
            redisUtil.set("redisTemplate","这是一条测试数据",10);
            String value = redisUtil.get("redisTemplate").toString();
            System.out.println("redisValue=" + value);
            // 成功，响应200
            return ResponseEntity.ok(value);
        } catch (Exception e) {
            e.printStackTrace();
            // 出错，响应500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     *@描述   验证redis是否会过期
     *@参数  [key]
     *@返回值  org.springframework.http.ResponseEntity<java.lang.String>
     *@创建人  huang.yj
     *@创建时间  2019/11/27
     */
    @GetMapping(value = "getKey")
    public ResponseEntity<String> getKey(String key){
        try {
            String value = (String) redisUtil.get(key);
            if (null != value) {
                System.out.println("key=" + key + ", value=" + value);
                // 成功，响应200
                return ResponseEntity.ok(value);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到该key或者value为空" );
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 出错，响应500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     *@描述   缓存一个对象，并反序列化回对象
     *@参数  []
     *@返回值  org.springframework.http.ResponseEntity<com.sample.test.common.entity.User>
     *@创建人  huang.yj
     *@创建时间  2019/11/27
     */
    @GetMapping("/setUser")
    public ResponseEntity<User> setUser(){
        try {
            User user = new User();
            user.setUserName("黄小红");
            user.setPhone("27819685");
            user.setRemarks("我是学生");
            redisUtil.set("user",user,10);

            User user2 = (User)redisUtil.get("user");
            System.out.println(user2.toString());
            // 成功，响应200
            return ResponseEntity.ok(user2);
        } catch (Exception e) {
            e.printStackTrace();
            // 出错，响应500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "moreTest")
    public ResponseEntity<Void> moreTest(String key){
        try {
            // 获取ValueOperations接口进行string操作，而不是每次都直接写成redisTemplate.opsForValue().方法名进行操作
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            // 缓存字符串，通过TimeUnit，设置有效时间为1分钟
            operations.set("redisTemplate-Str", "测试redisTemplate缓存字符串",1, TimeUnit.MINUTES);
            // 缓存对象，通过Duration，设置有效时间为2分钟
            operations.set("redisTemplate-Str2", "测试redisTemplate缓存字符串2",Duration.ofMinutes(2));

            // 获取ListOperations接口进行list操作，缓存一组数据
            ListOperations<String, Object> listOperations = redisTemplate.opsForList();
            listOperations.leftPushAll("names","李四","王五","赵六");
            listOperations.rightPushAll("names2","李四","王五","赵六");
            redisTemplate.expire("names",3,TimeUnit.MINUTES);

            // 获取HashOperations接口进行hash操作
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            Map<String,Object> testMap = new HashMap();
            testMap.put("name","jack");
            testMap.put("age",27);
            testMap.put("class","1");
            hashOperations.putAll("redisHash1",testMap);
            redisTemplate.expire("redisHash1",4,TimeUnit.MINUTES);

            // 获取setOperations接口进行set操作
            SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
            setOperations.add("setTest","aaa","bbb");
            redisTemplate.expire("setTest",5,TimeUnit.MINUTES);

            // 获取ZSetOperations接口进行zset操作
            ZSetOperations<String, Object> zsetOperations = redisTemplate.opsForZSet();
            ZSetOperations.TypedTuple<Object> objectTypedTuple1 = new DefaultTypedTuple<Object>("zset-5",9.6);
            ZSetOperations.TypedTuple<Object> objectTypedTuple2 = new DefaultTypedTuple<Object>("zset-6",9.9);
            Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<ZSetOperations.TypedTuple<Object>>();
            tuples.add(objectTypedTuple1);
            tuples.add(objectTypedTuple2);
            zsetOperations.add("zset1",tuples);
            redisTemplate.expire("zset1",6,TimeUnit.MINUTES);

            // 使用StringRedisTemplate进行字符串的操作
            ValueOperations<String, String> stringOperations = stringRedisTemplate.opsForValue();
            stringOperations.set("stringRedisTemplate_test", "stringRedisTemplate模板测试", 7, TimeUnit.MINUTES);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
            // 出错，响应500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}