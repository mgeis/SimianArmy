package com.netflix.simianarmy.local;

import java.util.List;

import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

/** Used locally to provide information about attackable instances.
 * @author mgeis
 *
 */
public interface InstanceCatalog {

    /**Returns info for all groups of instances visible to the SimianArmy.
     * @return List of instance groups, each of which has a list of LocalInstance
     */
    List<InstanceGroup> instanceGroups();

}
