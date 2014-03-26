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

import java.net.UnknownHostException;
import java.util.List;

import com.google.common.collect.Lists;

public class LocalInstanceGroup {
    private String id;
    private List<LocalInstance> instances;

    public LocalInstanceGroup() {
        super();
        instances = Lists.newArrayList();
    }

    public LocalInstanceGroup(String id) throws UnknownHostException {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LocalInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<LocalInstance> instances) {
        this.instances = instances;
    }

    public void addInstance(LocalInstance instance) {
        this.instances.add(instance);
    }

}
