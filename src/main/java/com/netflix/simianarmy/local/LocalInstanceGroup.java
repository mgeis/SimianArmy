package com.netflix.simianarmy.local;

import java.net.UnknownHostException;
import java.util.List;

import com.google.common.collect.Lists;

public class LocalInstanceGroup {
    private String id;
    private List<LocalInstance> instances;

    public LocalInstanceGroup() {
        super();
    }

    public LocalInstanceGroup(String id) throws UnknownHostException {
        this();
        this.id = id;
        instances = Lists.newArrayList();
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
