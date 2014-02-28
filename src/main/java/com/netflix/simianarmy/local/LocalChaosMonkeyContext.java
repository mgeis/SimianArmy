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

import com.netflix.simianarmy.basic.BasicChaosMonkeyContext;

/** AWS specific context for Chaos Monkey.  Provides AWS-centric
 * crawler and client, leaving basic chaos behavior to BasicChaosMonkeyContext.
 * @author mgeis
 *
 */
public class LocalChaosMonkeyContext extends BasicChaosMonkeyContext {

    /** Constructor.
     *
     */
    public LocalChaosMonkeyContext() {
        super();

        createClient();
        setChaosCrawler(new LocalChaosCrawler(localClient()));
    }
    
    /**
     * Gets the local client.
     * @return the local client
     */
    public LocalClient localClient() {
        return (LocalClient) cloudClient();
    }

    /**
     * Create the specific client with region taken from properties.
     * Override to provide your own client.
     */
    @Override
    protected void createClient() {
        setCloudClient(new LocalClient());
    }

}
