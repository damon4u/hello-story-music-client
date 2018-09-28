package com.randall.musicclient.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-08-28 10:49
 */
@Data
public class Proxy implements Serializable {
    
    private int id;

    private String ip;
    
    private int port;
    
    private String proto;      //http、https
    
    public Proxy() {}

    public Proxy(String ip, int port, String proto) {
        this.ip = ip;
        this.port = port;
        this.proto = proto.toLowerCase();
    }

    public String getProxyStr() {
        return proto + "://" + ip + ":" + port;
    }

}
