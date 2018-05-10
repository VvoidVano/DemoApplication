package com.example.abc.demoapplication.model;

/**
 * Created by yangyj on 2018/2/28
 */

public class BaseBean<T> {

    private String title;

    public T rows;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getRows() {
        return rows;
    }

    public void setRows(T rows) {
        this.rows = rows;
    }
}
