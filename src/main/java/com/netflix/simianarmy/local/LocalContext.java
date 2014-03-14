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
            "simianarmy.client.local.catalog.class", JsonFileInstanceCatalog.class.getName());
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
