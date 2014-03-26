/**
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
