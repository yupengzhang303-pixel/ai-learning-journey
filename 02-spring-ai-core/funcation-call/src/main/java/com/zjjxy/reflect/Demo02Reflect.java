package com.zjjxy.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Demo02Reflect {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = Student.class;

        // ========== 构造器 ==========
        System.out.println("===== 构造器 =====");
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> c : constructors) {
            System.out.println(c);
        }
        // 用无参构造器创建对象
        Object obj = clazz.getDeclaredConstructor().newInstance();

        // ========== 字段 ==========
        System.out.println("===== 字段 =====");
        Field nameField = clazz.getField("name");
        nameField.set(obj, "周甜");
        System.out.println("设置后: " + nameField.get(obj));

        // 私有字段要 setAccessible(true)
        Field ageField = clazz.getDeclaredField("age");
        ageField.setAccessible(true);
        ageField.setInt(obj, 22);
        System.out.println("私有字段 age: " + ageField.getInt(obj));

        System.out.println("完整对象: " + obj);

        // ========== 方法 ==========
        System.out.println("===== 方法 =====");
        Method greetMethod = clazz.getMethod("greet", String.class);
        Object result = greetMethod.invoke(obj, "张三");
        System.out.println("greet 返回值: " + result);

        Method calMethod = clazz.getMethod("cal", int.class, int.class);
        Object calResult = calMethod.invoke(obj, 10, 20);
        System.out.println("cal 返回值: " + calResult);
    }
}