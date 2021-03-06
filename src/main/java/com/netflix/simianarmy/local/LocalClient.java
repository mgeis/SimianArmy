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

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.ComputeService;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.proxy.internal.GuiceProxyConfig;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;
import com.netflix.simianarmy.CloudClient;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

/**A cloud client used for environments that constitute a "local cloud" (i.e., local servers
 * clustered behind a load balancer).
 * @author mgeis
 *
 */
public class LocalClient implements CloudClient {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalClient.class);

    /**The catalog of all machines visible to the client.
     */
    private InstanceCatalog catalog;

    /**Constructor.
     * @param localContext
     */
    public LocalClient(LocalContext localContext) {
        super();
        //create the catalog using its type and the location of its source data
        catalog = factory(localContext.getInstanceCatalogClass(), localContext.getInstanceCatalogLocation());
        LOGGER.info("Created catalog: " + catalog.getClass().getName());
    }

    /**Creates an instance of InstanceCatalog (exact flavor depends on how app is configured).
     * @param catalogClass
     * @param location
     * @return InstanceCatalog for use by LocalClient.
     */
    public <T extends InstanceCatalog> T factory(Class<T> catalogClass, String location) {
        try {
            if (location == null) {
                // assume InstanceCatalog class has a void constructor
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
        } catch (InstantiationException e) {
            LOGGER.error("local client factory error instantiating " + catalogClass.getName() + " from "
                + location, e);
        } catch (IllegalAccessException e) {
            LOGGER.error("access error instantiating " + catalogClass.getName() + " from "
                + location, e);
        } catch (Exception e) { //this is the fall-through for undeclared exceptions that may pop up
            LOGGER.error("local client factory error, cannot make instance catalog from " + catalogClass.getName()
                + " with " + location, e);
        }

        return null;
    }


    /** Should never be invoked, exists for interface compliance.
     * @throws UnsupportedOperationException if it is ever invoked
     * @see com.netflix.simianarmy.CloudClient#terminateInstance(java.lang.String)
     */
    @Override
    public void terminateInstance(String instanceId) {
        throw new UnsupportedOperationException("LocalClient does not directly terminate instance: uses script");
        // TODO note that with statically defined resources,
        // they may have previously been terminated and therefore "immune" to further attack
    }

    @Override
    public SshClient connectSsh(String instanceId, LoginCredentials credentials) {
        ProxyConfig pc = new GuiceProxyConfig();
        BackoffLimitedRetryHandler blrh = new BackoffLimitedRetryHandler();
        LocalInstance localInstance = lookupLocalInstance(instanceId);
        HostAndPort hap = HostAndPort.fromParts(localInstance.getHostName(), localInstance.getSshPort());
        SshClient ssh = new JschSshClient(pc, blrh, hap, credentials, 3000);
        ssh.connect();

        return ssh;
    }

    /**Get all information about an instance from its id.
     * @param id
     * @return The instance that corresponds to that id.
     */
    protected LocalInstance lookupLocalInstance(String id) {
        return catalog.getLocalInstanceFromId(id);
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
