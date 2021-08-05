package com.github.zhizuqiu.model;

public class GottyPath {
    private String path;

    public GottyPath() {
    }

    public GottyPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "GottyPath{" +
                "path='" + path + '\'' +
                '}';
    }
}

