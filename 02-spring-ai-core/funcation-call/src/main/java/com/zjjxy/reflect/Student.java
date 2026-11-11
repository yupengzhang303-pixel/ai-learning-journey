package com.zjjxy.reflect;

public class Student {

    public String name;
    private int age;

    public Student() {
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String greet(String greetName) {
        return "你好，" + greetName;
    }

    public String cal(int a, int b) {
        return "结果：" + (a + b);
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + "}";
    }
}