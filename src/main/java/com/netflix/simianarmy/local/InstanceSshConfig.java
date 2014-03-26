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

/**
 * @author mgeis
 *
 */
public class InstanceSshConfig extends GlobalSshConfig implements SshConfig {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalSshConfig.class);
    
    private final LoginCredentials localSshCredentials;
    
    
    public InstanceSshConfig(MonkeyConfiguration config, 
        String username, String password, String privateKeyPath) {
        super(config);
        
        String sshUser = (StringUtils.isNotEmpty(username)) ? 
            username : config.getStrOrElse("simianarmy.chaos.ssh.user", "root");
        String sshKeyPath = (StringUtils.isNotEmpty(privateKeyPath)) ? 
                privateKeyPath : config.getStrOrElse("simianarmy.chaos.ssh.key", null);
        String privateKey = getPrivateKeyContents(sshKeyPath);
        
        if (privateKey == null && StringUtils.isEmpty(password)) { //no key, no pass
            this.localSshCredentials = null;
        } else {
            LoginCredentials.Builder builder = LoginCredentials.builder().user(sshUser);
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

    @Override
    public LoginCredentials getCredentials() {
        return (localSshCredentials != null) ? localSshCredentials : getGlobalCredentials();
    }
    
    public LoginCredentials getGlobalCredentials() {
        return super.getCredentials();
    }

    @Override
    public boolean isEnabled() {
        return localSshCredentials != null || super.isEnabled();
    }

}
