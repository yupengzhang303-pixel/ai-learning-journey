package com.zjjxy;

import com.zjjxy.entity.Student;
import com.zjjxy.service.HashRedisService;
import com.zjjxy.service.ObjectRedisService;
import com.zjjxy.service.StringRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisService stringRedisService;

    @Autowired
    private HashRedisService hashRedisService;

    @Autowired
    private ObjectRedisService objectRedisService;

    @Test
    public void testString() {
        stringRedisService.cache("demo1", "hello-redis");
        String v = stringRedisService.get("demo1");
        System.out.println("取到: " + v);

        Long count = stringRedisService.incr("loves");
        System.out.println("自增后: " + count);

        boolean locked = stringRedisService.tryLock("lock:order:1", "user-1001");
        System.out.println("抢锁是否成功: " + locked);
    }

    @Test
    public void testHash() {
        Map<String, String> user1 = Map.of(
                "username", "刘轩婷",
                "age", "21",
                "address", "湖南-长沙"
        );
        Map<String, String> user2 = Map.of(
                "username", "龚星",
                "age", "22",
                "address", "湖南-张家界"
        );

        hashRedisService.saveUser("user1", user1);
        hashRedisService.saveUser("user2", user2);

        System.out.println("user2 的 name: " + hashRedisService.getField("user2", "username"));
        System.out.println("user1 全部信息: " + hashRedisService.getAll("user1"));
    }

    @Test
    public void testObject() {
        Student student = new Student();
        student.setId(1);
        student.setName("张于鹏");
        student.setAge(18);
        student.setScore(98);
        student.setAddress("湖南省");

        // 存入学生对象
        objectRedisService.cache("stu1", student);

        // 取出学生对象
        Student stu1 = objectRedisService.get("stu1", Student.class);
        System.out.println(stu1);
        // 预期输出: Student(id=1, name=张于鹏, age=18, score=98, address=湖南省)
    }
}