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
    
    List<LocalInstanceGroup> localInstanceGroups();
    
    LocalInstance getLocalInstanceFromId(String id);

}
