package com.netflix.simianarmy.local;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.netflix.simianarmy.basic.chaos.BasicInstanceGroup;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

public class JsonFileInstanceCatalog implements InstanceCatalog {
    String configFile = "./instance_groups.json";

    public JsonFileInstanceCatalog(String configFile) {
        if (StringUtils.isNotBlank(configFile)) {
            this.configFile = configFile;
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
            localGroups = mapper.readValue(new File(configFile), new TypeReference<List<LocalInstanceGroup>>() { } );

            for (LocalInstanceGroup localGroup : localGroups) {
                System.out.println("parsing local instance group " 
                    + localGroup.getId() + " with " + localGroup.getInstances().size() + " instances");
                BasicInstanceGroup big = new BasicInstanceGroup(
                    localGroup.getId(), LocalChaosCrawler.Types.LOCAL, null);
                igs.add(big);
                for (LocalInstance localInstance : localGroup.getInstances()) {
                    System.out.println("parsing local instance " + localInstance.getId() 
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
