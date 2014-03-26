/*
 *
 *  Copyright 2014 Salesforce.com, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.simianarmy.local;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalInstance {
    private String id;
    private InetAddress address;
    private String username;
    private String password; //used for either username or to unlock private key
    private String privateKeyFilePath;
    private Integer sshPort = new Integer(22);

    public LocalInstance() {
        super();
    }

    public LocalInstance(String id, String ipOrHostname) throws UnknownHostException {
        this();
        this.id = id;
        this.address = InetAddress.getByName(ipOrHostname);
    }
    
    public LocalInstance(String id, String ipOrHostname, Integer sshPort) throws UnknownHostException {
        this(id, ipOrHostname);
        if (sshPort != null) {
            this.sshPort = sshPort;
        }
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

   public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

}
