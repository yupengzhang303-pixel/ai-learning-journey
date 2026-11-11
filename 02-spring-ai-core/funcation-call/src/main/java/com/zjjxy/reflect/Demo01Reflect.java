package com.zjjxy.reflect;

public class Demo01Reflect {

    public static void main(String[] args) throws Exception {
        // 方式 1：Class.forName("全限定名")
        Class<?> cls1 = Class.forName("com.zjjxy.reflect.Student");
        System.out.println("方式1: " + cls1);

        // 方式 2：类名.class
        Class<?> cls2 = Student.class;
        System.out.println("方式2: " + cls2);

        // 方式 3：对象.getClass()
        Student student = new Student();
        Class<?> cls3 = student.getClass();
        System.out.println("方式3: " + cls3);

        // 三种方式拿到的都是同一个 Class 对象
        System.out.println("cls1 == cls2: " + (cls1 == cls2));
        System.out.println("cls2 == cls3: " + (cls2 == cls3));
    }
}