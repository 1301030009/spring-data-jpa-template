package com.huanan9527.spring.data.domain.ext;

/**
 * Project  : monkey
 * Author   : Wu Tian Qiang
 * Date     : 2016/9/1
 */
public enum Status {
    ENABLE("启用"),
    DISABLE("禁用");

    private String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
