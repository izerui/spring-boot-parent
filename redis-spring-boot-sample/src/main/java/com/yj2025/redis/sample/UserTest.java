package com.yj2025.redis.sample;

import java.util.Date;

/**
 * @author leiyang
 * @since 2021/12/1 10:51
 */
public class UserTest {
    private Integer id;
    private String name;
    private Date birthday;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public UserTest(Integer id, String name, Date birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public UserTest() {
    }

    @Override
    public String toString() {
        return "UserTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
