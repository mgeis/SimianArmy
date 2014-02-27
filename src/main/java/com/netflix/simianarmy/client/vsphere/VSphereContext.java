/*
 *  Copyright 2012 Immobilien Scout GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.simianarmy.client.vsphere;

import com.netflix.simianarmy.MonkeyConfiguration;
import com.netflix.simianarmy.aws.AWSChaosMonkeyContext;

/**
 * This Context extends the BasicContext in order to provide a different client: the VSphereClient.
 *
 * @author ingmar.krusch@immobilienscout24.de
 */
public class VSphereContext extends AWSChaosMonkeyContext {
    @Override
    protected void createClient() {
        MonkeyConfiguration config = configuration();
        final PropertyBasedTerminationStrategy terminationStrategy = new PropertyBasedTerminationStrategy(config);
        final VSphereServiceConnection connection = new VSphereServiceConnection(config);
        final VSphereClient client = new VSphereClient(terminationStrategy, connection);
        setCloudClient(client);
    }
}
