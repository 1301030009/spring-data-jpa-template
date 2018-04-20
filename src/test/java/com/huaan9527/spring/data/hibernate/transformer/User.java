package com.huaan9527.spring.data.hibernate.transformer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
    private String name;
    private Integer age;

    private Address address;

    private String personLike;

    private Map<String, Object> attrs;

    private Set<Address> set;

    private List<Address> list;

    private Set<Map<String, Object>> setmap;

    public User() {
    }

    public Set<Map<String, Object>> getSetmap() {
        return setmap;
    }

    public void setSetmap(Set<Map<String, Object>> setmap) {
        this.setmap = setmap;
    }

    public List<Address> getList() {
        return list;
    }

    public void setList(List<Address> list) {
        this.list = list;
    }

    public Set<Address> getSet() {
        return set;
    }

    public void setSet(Set<Address> set) {
        this.set = set;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    public String getPersonLike() {
        return personLike;
    }

    public void setPersonLike(String personLike) {
        this.personLike = personLike;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }




}
