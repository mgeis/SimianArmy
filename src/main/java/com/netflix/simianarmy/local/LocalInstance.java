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

/** Model to represent instances that can be found in a local cloud.  Contains
 * id, location, and information necessary for access to the machine.
 * @author mgeis
 *
 */
public class LocalInstance {
    private String id;
    private InetAddress address;
    private String username;
    private String password; //used for either username or to unlock private key
    private String privateKeyFilePath;
    private Integer sshPort = new Integer(22); //default is the default SSH port number

    /** Default constructor. */
    public LocalInstance() {
        super();
    }

    /** Constructor that takes the basic info necessary to use the group.
     * @param id id of the instance
     * @param ipOrHostname DNS-locatable hostname or direct IP address of instance
     * @throws UnknownHostException if no IP address for the host could be found.
     */
    public LocalInstance(String id, String ipOrHostname) throws UnknownHostException {
        this();
        this.id = id;
        this.address = InetAddress.getByName(ipOrHostname);
    }

    /** Constructor that takes connection info to use.
     * @param id id of the instance.
     * @param ipOrHostname DNS-locatable hostname or direct IP address of instance
     * @param sshPort Port to make SSH connections to.  Will only accept a non-null value.
     * If value is null, instance will use default SSH port number (22).
     * @throws UnknownHostException if no IP address for the host could be found.
     */
    public LocalInstance(String id, String ipOrHostname, Integer sshPort) throws UnknownHostException {
        this(id, ipOrHostname);
        if (sshPort != null) {
            this.sshPort = sshPort;
        }
    }

    /** Accessor for id.
     * @return the id of the local instance
     */
    public String getId() {
        return id;
    }

    /** Mutator for the id.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /** Accessor for the address of the instance.
     * @return the address
     */
    public InetAddress getAddress() {
        return address;
    }

    /** Mutator for the address of the instance.
     * @param address
     */
    public void setAddress(InetAddress address) {
        this.address = address;
    }

    /** Convenience method.  Under the hood, address construction can take either a hostname
     * or an IP address, but this method signature makes it very clear what is allowed.
     * @param hostname
     * @throws UnknownHostException if no IP address for the host could be found.
     */
    public void setHostname(String hostname) throws UnknownHostException {
        address = InetAddress.getByName(hostname);
    }

    /** Convenience method.  Under the hood, address construction can take either a hostname
     * or an IP address, but this method signature makes it very clear what is allowed.
     * @param ip
     * @throws UnknownHostException if no IP address for the host could be found.
     */
    public void setIpAddress(String ip) throws UnknownHostException {
        setHostname(ip);
    }

    /** Accessor for instance hostname.  Derived from reverse lookup or from explicitly provided hostname.
     * @return
     */
    public String getHostName() {
        return address.getHostName();
    }

    /** Accessor for IP address (from explicitly provided IP or derived from provided hostname).
     * @return
     */
    public String getIpAddress() {
        return address.getHostAddress();
    }

    /** Accessor for the username for the instance.
     * @return the username used for login
     */
    public String getUsername() {
        return username;
    }

    /** Mutator for username to log in to instance.
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Accessor for the password of the instance's user or private key file.
     * @return the user or key file password.
     */
    public String getPassword() {
        return password;
    }

    /** Mutator for instance password for user or private key file.
     * @param password password for user or private key.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Accessor for the path to the private key of the instance.
     * @return The relative or absolute path to the key (as defined by constructor or setter).  Can be null.
     */
    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    /** Mutator for path to private key file for instance login.
     * @param privateKeyFilePath
     */
    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    /** Accessor for the ssh port of the instance.
     * @return the ssh port number (22 is the default).
     */
    public Integer getSshPort() {
        return sshPort;
    }

    /** Mutator for the ssh port of the instance.
     * @param sshPort
     */
    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

}
