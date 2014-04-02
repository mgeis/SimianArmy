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

import org.apache.commons.lang.StringUtils;
import org.jclouds.domain.LoginCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.simianarmy.MonkeyConfiguration;
import com.netflix.simianarmy.chaos.GlobalSshConfig;
import com.netflix.simianarmy.chaos.SshConfig;

/**Configuration information for a specific machine instance.  This configuration should be
 * used when a global configuration is not available (global is where one username and
 * one private key works for every single instance visible to the simian army).
 * @author mgeis
 *
 */
public class InstanceSshConfig extends GlobalSshConfig implements SshConfig {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalSshConfig.class);

    /**Memoized login info for the instance.
     */
    private final LoginCredentials localSshCredentials;


    /**The constructor.
     * @param config The Simian Army config that holds runtime property values
     * @param username Name of user to log in as
     * @param password user password or private key file password
     * @param privateKeyPath nullable private key location
     */
    public InstanceSshConfig(MonkeyConfiguration config,
        String username, String password, String privateKeyPath) {
        //build a global config, just in case it can be used.
        //it's possible that only one instance out of many is locally configured
        super(config);

        //if the local username is defined, use it.
        //otherwise, use the property, and if that's empty, use root
        String sshUser = (StringUtils.isNotEmpty(username))
            ? username : config.getStrOrElse("simianarmy.chaos.ssh.user", "root");
        //if local keypath is defined it, use it.  Otherwise, use global path, or null
        String sshKeyPath = (StringUtils.isNotEmpty(privateKeyPath))
            ? privateKeyPath : config.getStrOrElse("simianarmy.chaos.ssh.key", null);
        String privateKey = getPrivateKeyContents(sshKeyPath);

        //if there's no private key and no pass, there are no local credentials
        if (privateKey == null && StringUtils.isEmpty(password)) { //no key, no pass
            this.localSshCredentials = null;
        } else { //start building the local credentials
            LoginCredentials.Builder builder = LoginCredentials.builder().user(sshUser);
            //if the user is not root, we know we'll need to sudo for some of our script commands
            if (!StringUtils.equals("root", sshUser)) {
                LOGGER.info("should auth as sudo");
                builder.authenticateSudo(true);
            }
            if (StringUtils.isNotEmpty(password)) {
                builder.password(password);
            } else {
                builder.noPassword();
            }

            if (StringUtils.isEmpty(privateKey)) {
                builder.noPrivateKey();
            } else {
                builder.privateKey(privateKey);
            }

            this.localSshCredentials = builder.build();
        }

    }

    /** If there are local credentials, use those, otherwise use the global credentials.
     * @see com.netflix.simianarmy.chaos.GlobalSshConfig#getCredentials()
     */
    @Override
    public LoginCredentials getCredentials() {
        return (localSshCredentials != null) ? localSshCredentials : getGlobalCredentials();
    }

    /**Return the credentials created by the system property values.
     * @return the credentials created by the system property values
     */
    public LoginCredentials getGlobalCredentials() {
        return super.getCredentials();
    }

    /**Indicates whether SSH connectivity is enabled.
     * @return true if local or global ssh credentials are defined, return true, otherwise return false
     * @see com.netflix.simianarmy.chaos.GlobalSshConfig#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return localSshCredentials != null || super.isEnabled();
    }

}
