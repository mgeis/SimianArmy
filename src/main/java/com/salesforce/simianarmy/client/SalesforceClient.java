package com.salesforce.simianarmy.client;

import java.util.List;
import java.util.Map;

import org.jclouds.compute.ComputeService;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;

import com.google.common.net.HostAndPort;
import com.netflix.simianarmy.CloudClient;

public class SalesforceClient implements CloudClient {

    
    public SalesforceClient(String region) {
        super();
    }

    @Override
    public void terminateInstance(String instanceId) {
        // TODO IMPLEMENT THIS
    }

    @Override
    public void deleteAutoScalingGroup(String asgName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteLaunchConfiguration(String launchConfigName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteVolume(String volumeId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteSnapshot(String snapshotId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteImage(String imageId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createTagsForResources(Map<String, String> keyValueMap, String... resourceIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> listAttachedVolumes(String instanceId, boolean includeRoot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachVolume(String instanceId, String volumeId, boolean force) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ComputeService getJcloudsComputeService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getJcloudsId(String instanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SshClient connectSsh(String instanceId, LoginCredentials credentials) {
        // TODO IMPLEMENT THIS, but maybe not as jclouds ssh.  Nice to have,
        //as it's a clean abstraction, but that might not be possible.  need to research
        //instance id can be an id of an object that wraps metadata about a server
        
        ProxyConfig pc = null;
        BackoffLimitedRetryHandler blrh = null;
        HostAndPort hap = HostAndPort.fromString("");//TODO fill in hostname, port here
        LoginCredentials lc = LoginCredentials.builder().user("sfdc_ops").noPassword().privateKey("").build();
        SshClient ssh = new JschSshClient(pc, blrh, hap, lc, 3000);
        ssh.connect();

        return ssh;
    }

    @Override
    public String findSecurityGroup(String instanceId, String groupName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String createSecurityGroup(String instanceId, String groupName, String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canChangeInstanceSecurityGroups(String instanceId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInstanceSecurityGroups(String instanceId, List<String> groupIds) {
        throw new UnsupportedOperationException();
    }


}
