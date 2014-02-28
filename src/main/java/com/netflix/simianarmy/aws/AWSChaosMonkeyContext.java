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
package com.netflix.simianarmy.aws;

import com.netflix.simianarmy.MonkeyRecorder;
import com.netflix.simianarmy.basic.BasicChaosMonkeyContext;
import com.netflix.simianarmy.client.aws.AWSClient;
import com.netflix.simianarmy.client.aws.chaos.ASGChaosCrawler;

/** AWS specific context for Chaos Monkey.  Provides AWS-centric
 * crawler and client, leaving basic chaos behavior to BasicChaosMonkeyContext.
 * @author mgeis
 *
 */
public class AWSChaosMonkeyContext extends BasicChaosMonkeyContext {
    private final AWSContext awsContext;

    /** Constructor.
     *
     */
    public AWSChaosMonkeyContext() {
        super();

        awsContext = new AWSContext(configuration());
        createClient();
        setChaosCrawler(new ASGChaosCrawler(awsClient()));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void createRecorder() {
        @SuppressWarnings("rawtypes")
        Class recorderClass = loadClientClass("simianarmy.client.recorder.class");
        if (recorderClass == null || recorderClass.equals(SimpleDBRecorder.class)) {
            String domain = configuration().getStrOrElse("simianarmy.recorder.sdb.domain", "SIMIAN_ARMY");
            if (cloudClient() != null && cloudClient().getClass() == AWSClient.class) {
                SimpleDBRecorder simpleDbRecorder = new SimpleDBRecorder(awsClient(), domain);
                simpleDbRecorder.init();
                setRecorder(simpleDbRecorder);
            }
        } else {
            setRecorder((MonkeyRecorder) factory(recorderClass));
        }
    }

    /**
     * Create the specific client with region taken from properties.
     * Override to provide your own client.
     */
    @Override
    protected void createClient() {
        setCloudClient(new AWSClient(awsContext.region(), awsContext.getAwsCredentialsProvider()));
    }

    /**
     * Gets the AWS client.
     * @return the AWS client
     */
    public AWSClient awsClient() {
        return (AWSClient) cloudClient();
    }

}
