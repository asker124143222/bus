package com.home.bus.entity.esIndex;

import java.io.Serializable;

public class ESCustomer implements Serializable {
    private long id;
    private String name;
    private String addr;
    private String tel;

    public ESCustomer() {
    }

    public ESCustomer(long id, String name, String addr, String tel) {
        this.id = id;
        this.name = name;
        this.addr = addr;
        this.tel = tel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
