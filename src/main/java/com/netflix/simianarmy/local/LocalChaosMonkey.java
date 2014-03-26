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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.simianarmy.CloudClient;
import com.netflix.simianarmy.basic.chaos.BasicChaosMonkey;
import com.netflix.simianarmy.chaos.ChaosInstance;
import com.netflix.simianarmy.chaos.ChaosMonkey;
import com.netflix.simianarmy.chaos.ChaosType;
import com.netflix.simianarmy.chaos.ShutdownInstanceChaosType;
import com.netflix.simianarmy.chaos.ShutdownLocalInstanceChaosType;


/**
 * The Class BasicChaosMonkey.
 */
public class LocalChaosMonkey extends BasicChaosMonkey {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalChaosMonkey.class);

    /**
     * Instantiates a new basic chaos monkey.
     * @param ctx
     *            the ctx
     */
    public LocalChaosMonkey(ChaosMonkey.Context ctx) {
        super(ctx);
        allChaosTypes.add(new ShutdownLocalInstanceChaosType(cfg));
        //now remove the cloud-specific chaos types
        for (Iterator<ChaosType> iterator = allChaosTypes.iterator(); iterator.hasNext();) {
            ChaosType chaosType = iterator.next();
            if (ShutdownInstanceChaosType.class == chaosType.getClass()) {
                iterator.remove();
            }
        }
        
    }

        //TODO: if the chaos type is a script based one for a local machine, we
        //will need to log in using more than just the standard SshConfig.
        //we may need a beefed up version of ssh config
        //at the very least we need something that goes beyond one-size-fits-all
        
        
        //to create a global ssh config, we only need the global monkey configuration
        //to create an instance-specific one, we need local instance info.
        //because chaos instance is really just a wrapper, we may need to subclass it
        //and specify the chaos instance class.
        
            
    @Override
    protected ChaosInstance getChaosInstance(CloudClient cloudClient, String instanceId) {
        LocalClient localClient = (LocalClient)cloudClient;
        LocalInstance instance = localClient.lookupLocalInstance(instanceId);
        InstanceSshConfig sshConfig = new InstanceSshConfig(
            cfg, instance.getUsername(), instance.getPassword(), instance.getPrivateKeyFilePath());
        ChaosInstance chaosInstance = new ChaosInstance(context().cloudClient(), instanceId, sshConfig);
        return chaosInstance;
    }
    
}