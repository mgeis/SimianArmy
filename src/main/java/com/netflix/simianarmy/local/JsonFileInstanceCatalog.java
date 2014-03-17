package com.netflix.simianarmy.local;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.netflix.simianarmy.basic.chaos.BasicInstanceGroup;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

public class JsonFileInstanceCatalog implements InstanceCatalog {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileInstanceCatalog.class);
    
    String configFile = "./instance_groups.json";

    public JsonFileInstanceCatalog(String configFile) {
        if (StringUtils.isNotBlank(configFile)) {
            this.configFile = configFile;
            List<InstanceGroup> igs = instanceGroups();//do this just to check logs
            //maybe cache it later
        }
    }

//    String data =
//"[" +
//    "{" +
//        "\"id\": \"appServers\", " +
//        "\"instances\" : " +
//            "[" +
//                "{" +
//                    "\"ipAddress\": \"127.0.0.2\"," +
//                    "\"id\": \"instance1\"" +
//                "}," +
//                "{" +
//                    "\"ipAddress\": \"127.0.0.3\"," +
//                    "\"id\": \"instance2\"" +
//                "}" +
//            "]" +
//    "}" +
//"]";
//
//    public static void main(String[] args) {
//        JsonFileInstanceCatalog test = new JsonFileInstanceCatalog(null);
//        List<InstanceGroup> groups = test.instanceGroups();
//        for (InstanceGroup instanceGroup : groups) {
//            System.out.println(instanceGroup.name());
//            for (String s : instanceGroup.instances()) {
//                System.out.println("\t" + s);
//            }
//        }
//    }

    @Override
    public List<InstanceGroup> instanceGroups() {
        ObjectMapper mapper = new ObjectMapper();
        List<LocalInstanceGroup> localGroups = null;
        List<InstanceGroup> igs = Lists.newArrayList();
        try {
            File instanceConfiguration = new File(configFile);
            LOGGER.info(this.getClass().getName() + " reading instance file at " 
                + instanceConfiguration.getCanonicalPath());
            localGroups = mapper.readValue(instanceConfiguration, new TypeReference<List<LocalInstanceGroup>>() { });

            for (LocalInstanceGroup localGroup : localGroups) {
                LOGGER.info("parsing local instance group "
                    + localGroup.getId() + " with " + localGroup.getInstances().size() + " instances");
                BasicInstanceGroup big = new BasicInstanceGroup(
                    localGroup.getId(), LocalChaosCrawler.Types.LOCAL, null);
                igs.add(big);
                for (LocalInstance localInstance : localGroup.getInstances()) {
                    LOGGER.info("parsing local instance " + localInstance.getId()
                        + " at " + localInstance.getHostName());
                    big.addInstance(localInstance.getId());
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return igs;
    }

}
