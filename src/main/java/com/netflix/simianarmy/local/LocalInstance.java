package com.netflix.simianarmy.local;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalInstance {
    private String id;
    private InetAddress address;
    private String username;
    private String userpass;
    private String privateKeyFilePath;
    private String privateKeyFilePassword;

    public LocalInstance() {
        super();
    }

    public LocalInstance(String id, String ipOrHostname) throws UnknownHostException {
        this();
        this.id = id;
        this.address = InetAddress.getByName(ipOrHostname);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setHostname(String hostname) throws UnknownHostException {
        address = InetAddress.getByName(hostname);
    }

    public void setIpAddress(String ip) throws UnknownHostException {
        setHostname(ip);
    }

    public String getHostName() {
        return address.getHostName();
    }

    public String getIpAddress() {
        return address.getHostAddress();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public String getPrivateKeyFilePassword() {
        return privateKeyFilePassword;
    }

    public void setPrivateKeyFilePassword(String privateKeyFilePassword) {
        this.privateKeyFilePassword = privateKeyFilePassword;
    }

}
