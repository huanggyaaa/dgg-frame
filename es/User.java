package net.dgg.accountingtools.common;

import net.dgg.framework.tac.elasticsearch.annotation.DggEsIdentify;

import java.util.Date;

/**
 * @description: es 测试类
 * @author: huanggy
 * @date: 2019/2/25
 **/
public class User {
    /**
     * 批量插入必须使用该注解标注 ID 字段
     */
    @DggEsIdentify
    private Long id;
    private String userName;
    private String password;
    private Date born;
    private Integer age;
    private String addr;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", born=" + born +
                ", age=" + age +
                ", addr='" + addr + '\'' +
                '}';
    }

    public User(Long id, String userName, String password, Date born, Integer age, String addr) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.born = born;
        this.age = age;
        this.addr = addr;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBorn() {
        return born;
    }

    public void setBorn(Date born) {
        this.born = born;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
