package com.netflix.simianarmy.chaos;

import org.jclouds.domain.LoginCredentials;

public interface SshConfig {

    /**
     * Get the configured SSH credentials.
     *
     * @return configured SSH credentials
     */
    public abstract LoginCredentials getCredentials();

    /**
     * Check if ssh is configured.
     *
     * @return true if credentials are configured
     */
    public abstract boolean isEnabled();

}