package com.vonchange.nine.demo.domain;

//@Data
public class UserBaseVO {
    private int id;
    private String userName;
    private String  firstPhone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstPhone() {
        return firstPhone;
    }

    public void setFirstPhone(String firstPhone) {
        this.firstPhone = firstPhone;
    }

    @Override
    public String toString() {
        return "UserBaseVO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", firstPhone='" + firstPhone + '\'' +
                '}';
    }
}
