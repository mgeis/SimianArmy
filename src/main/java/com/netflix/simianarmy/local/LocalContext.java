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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.simianarmy.MonkeyConfiguration;

/** Wrapper class for Local-specific details used by Local monkey contexts.
 * @author mgeis
 *
 */
public class LocalContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalContext.class);

    Class<? extends InstanceCatalog> instanceCatalogClass;
    String instanceCatalogLocation;

    /** Constructor.
     * @param config
     * @throws ClassNotFoundException
     */
    public LocalContext(MonkeyConfiguration config) throws ClassNotFoundException {
        String instanceCatalogClassName = config.getStrOrElse(
            "simianarmy.client.local.catalog.class", JsonInstanceCatalog.class.getName());
        instanceCatalogClass = loadClientClass(instanceCatalogClassName);
        instanceCatalogLocation = config.getStrOrElse(
            "simianarmy.client.local.catalog.location", "./instance_catalog.json");
    }

    //TODO: refactor, this is almost a full copy/paste code
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Class<? extends InstanceCatalog> loadClientClass(String className) throws ClassNotFoundException  {
        ClassLoader classLoader = LocalContext.class.getClassLoader();
        Class newClass = classLoader.loadClass(className);
        LOGGER.info("loaded " + newClass.getCanonicalName());
        return newClass;
    }

    public Class<? extends InstanceCatalog> getInstanceCatalogClass() {
        return instanceCatalogClass;
    }

    public void setInstanceCatalogClass(
            Class<? extends InstanceCatalog> instanceCatalogClass) {
        this.instanceCatalogClass = instanceCatalogClass;
    }

    public String getInstanceCatalogLocation() {
        return instanceCatalogLocation;
    }

    public void setInstanceCatalogLocation(String instanceCatalogLocation) {
        this.instanceCatalogLocation = instanceCatalogLocation;
    }


}
