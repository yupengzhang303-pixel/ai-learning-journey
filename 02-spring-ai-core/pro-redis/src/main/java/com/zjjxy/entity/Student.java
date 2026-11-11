package com.zjjxy.entity;

import java.io.Serializable;

public class Student implements Serializable {

    private Integer id;
    private String name;
    private Integer age;
    private Integer score;
    private String address;

    public Student() {
    }

    public Student(Integer id, String name, Integer age, Integer score, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.score = score;
        this.address = address;
    }

    // getter / setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Student(id=" + id + ", name=" + name +
               ", age=" + age + ", score=" + score +
               ", address=" + address + ")";
    }
}