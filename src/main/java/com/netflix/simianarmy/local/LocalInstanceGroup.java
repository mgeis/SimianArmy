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

import java.util.List;

import com.google.common.collect.Lists;

/**Information about a group of instances used by a local cloud.
 * An example of this would be a cluster of application servers, cache
 * servers, or Oracle RAC nodes.
 * @author mgeis
 */
public class LocalInstanceGroup {
    private String id;
    private List<LocalInstance> instances;

    /**Default constructor.
     */
    public LocalInstanceGroup() {
        super();
        instances = Lists.newArrayList();
    }

    /**Constructor that takes an id for the group.
     * @param id (example : "appservers")
     */
    public LocalInstanceGroup(String id) {
        this();
        this.id = id;
    }

    /**Accessor for id of the group.
     * @return the id of the group
     */
    public String getId() {
        return id;
    }

    /**Mutator for id of the group.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**Get all data for instances in the group.
     * @return A list of LocalInstance
     */
    public List<LocalInstance> getInstances() {
        return instances;
    }

    /**Set all instances for the group.
     * @param instances
     */
    public void setInstances(List<LocalInstance> instances) {
        this.instances = instances;
    }

    /**Add a single LocalInstance to the group.
     * @param instance
     */
    public void addInstance(LocalInstance instance) {
        this.instances.add(instance);
    }

}
