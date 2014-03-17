package com.netflix.simianarmy.local;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.ComputeService;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.netflix.simianarmy.CloudClient;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

public class LocalClient implements CloudClient {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalClient.class);

    private InstanceCatalog catalog;

    public LocalClient(LocalContext localContext) {
        super();
        catalog = factory(localContext.instanceCatalogClass, localContext.getInstanceCatalogLocation());
        LOGGER.info("Created catalog: " + catalog.getClass().getName());
    }

    public <T extends InstanceCatalog> T factory(Class<T> catalogClass, String location) {
        try {
            if (location == null) {
                // assume InstanceCatalog class has has void ctor
                return catalogClass.newInstance();
            }

            // then find corresponding ctor
            for (Constructor<?> ctor : catalogClass.getDeclaredConstructors()) {
                Class<?>[] paramTypes = ctor.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                T cat = (T) ctor.newInstance(location);
                return cat;
            }
        } catch (Exception e) {
            LOGGER.error("monkeyFactory error, cannot make instance catalog from " + catalogClass.getName() + " with "
                    + (location == null ? null : location), e);
        }

        return null;
    }


    @Override
    public void terminateInstance(String instanceId) {
        LOGGER.info("Executing takedown of instance " + instanceId);
        // TODO IMPLEMENT THIS
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

    /**
     * Describe auto scaling groups.
     *
     * @return the list
     */
    public List<InstanceGroup> describeInstanceGroups() {
        return describeInstanceGroups((String[]) null);
    }

    /**
     * Describe a set of specific auto scaling groups.
     *
     * @param names the ASG names
     * @return the auto scaling groups
     */
    public List<InstanceGroup> describeInstanceGroups(String... names) {
        if (names == null || names.length == 0) {
            LOGGER.info("Getting all instance groups.");
        } else {
            LOGGER.info(String.format("Getting auto-scaling groups for %d names.", names.length));
        }

        //now query the instance catalog
        //interface instance catalog,
        //jsonfileinstancecatalog, jsonrestfulcatalog

        List<InstanceGroup> isgs = catalog.instanceGroups();
        LOGGER.info(String.format("Got %d instance groups.", isgs.size()));
        return isgs;
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
