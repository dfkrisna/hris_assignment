package com.pusilkom.hris.dto.form.cmd;

import java.util.List;

public class AuthItemCmd {

    private Long id;
    private String name;
    private Integer type;
    private String description;
    private List<String> childList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getChildList() {
        return childList;
    }

    public void setChildList(List<String> childList) {
        this.childList = childList;
    }
}
