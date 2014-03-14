package com.netflix.simianarmy.local;

import java.util.List;

import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

/** Used locally to provide information about attackable instances.
 * @author mgeis
 *
 */
public interface InstanceCatalog {

    List<InstanceGroup> instanceGroups();

}
