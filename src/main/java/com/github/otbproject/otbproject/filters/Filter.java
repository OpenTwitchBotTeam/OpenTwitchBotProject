package com.github.otbproject.otbproject.filters;

public class Filter {
    private String data;
    private FilterType type;
    private String group;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}