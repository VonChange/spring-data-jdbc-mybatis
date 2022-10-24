package com.vonchange.mybatis.dialect;

public class LikeTemplate {
    private String full;
    private String left;
    private String right;

    public LikeTemplate(String full, String left, String right) {
        this.full = full;
        this.left = left;
        this.right = right;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
