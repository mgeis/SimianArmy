package com.netflix.simianarmy.local;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.netflix.simianarmy.basic.chaos.BasicInstanceGroup;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

/** Consumes instance descriptions from JSON file.  File will look like this:
 * 
 * [    
       {
           "id": "appServers",
           "instances" : 
              [
                   {
                      "ipAddress": "127.0.0.2",
                      "id": "instance1"
                      "username" : "foo",
                      "userpass" : "bar"
                   },
                   {
                      "ipAddress": "127.0.0.3",
                      "id": "instance2",
                      "privateKeyFilePath" : "/path/to/privateKeyWithPassword",
                      "privateKeyFilePassword" : "k3yP@55w0rd"
                   },
                   {
                      "ipAddress": "127.0.0.4",
                      "id": "instance3",
                      "privateKeyFilePath" : "/path/to/privateKeyWithNoPassword"
                   }
              ]
        },
        {
           "id": "dbServers",
           "instances" : 
              [
                   {
                      "ipAddress": "127.0.0.5",
                      "id": "instance4"
                      "username" : "scott",
                      "userpass" : "tiger"
                   }
              ]
        }
    ]
 * 
 * 
 * @author mgeis
 *
 */
public class JsonFileInstanceCatalog implements InstanceCatalog {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileInstanceCatalog.class);
    
    String configFile = "./instance_groups.json";

    public JsonFileInstanceCatalog(String configFile) {
        if (StringUtils.isNotBlank(configFile)) {
            this.configFile = configFile;
            try {
                LOGGER.info(this.getClass().getName() + " reading instance file at " 
                    + (new File(configFile)).getCanonicalPath());
            } catch (IOException e) {
                LOGGER.error("unable to parse instance catalog at " + configFile, e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<InstanceGroup> instanceGroups() {
        ObjectMapper mapper = new ObjectMapper();
        List<LocalInstanceGroup> localGroups = null;
        List<InstanceGroup> instanceGroups = Lists.newArrayList();
        try {
            File instanceConfiguration = new File(configFile);
            LOGGER.info(this.getClass().getName() + " reading instance file at " 
                + instanceConfiguration.getCanonicalPath());
            localGroups = mapper.readValue(instanceConfiguration, new TypeReference<List<LocalInstanceGroup>>() { });
        } catch (Exception e) {
            LOGGER.error("Could not consume JSON at " + configFile, e);
            return instanceGroups;
        }
        for (LocalInstanceGroup localGroup : localGroups) {
            LOGGER.info("parsing local instance group "
                + localGroup.getId() + " with " + localGroup.getInstances().size() + " instances");
            BasicInstanceGroup big = new BasicInstanceGroup(
                localGroup.getId(), LocalChaosCrawler.Types.LOCAL, null);
            instanceGroups.add(big);
            for (LocalInstance localInstance : localGroup.getInstances()) {
                LOGGER.info("parsing local instance " + localInstance.getId()
                    + " at " + localInstance.getHostName());
                big.addInstance(localInstance.getId());
            }
        }
        return instanceGroups;
    }

}
