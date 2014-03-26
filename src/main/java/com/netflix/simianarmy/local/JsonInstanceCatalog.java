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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.simianarmy.basic.chaos.BasicInstanceGroup;
import com.netflix.simianarmy.chaos.ChaosCrawler.InstanceGroup;

/** Consumes instance descriptions from JSON stream (file, URL, or classpath location).
 * 
 * File will look like this:
 * 
 * Notes:
 * 1.  Each instance group has an id and an array of instances.
 * 2.  Each instance must have an id, as well as an ipAddress or hostname.
 * 3.  The instance's sshPort is assumed to be 22, and is optional.
 * 4.  Login credentials must be provided.  This should be a user on the sudo-ers list
 *     and the credentials can be one of the following
 *         a) password 
 *         b) private-key file 
 *         c) password-protected private key file
 * 5.  The id of a group is used in chaos.properties the same way as an ASG name is used
 *     for purposes of enabling a group for chaos.        
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
                      "userpass" : "bar",
                      "sshPort" : 22
                   },
                   {
                      "hostname": "devtest.mydomain.com",
                      "id": "instance2",
                      "privateKeyFilePath" : "/path/to/privateKeyWithPassword",
                      "privateKeyFilePassword" : "k3yP@55w0rd"
                   },
                   {
                      "ipAddress": "127.0.0.4",
                      "id": "instance3",
                      "privateKeyFilePath" : "/path/to/privateKeyWithNoPassword",
                      "sshPort" : 23
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
//
//thoughts here:
//    regular simian army assumes there is ONE private key with a matching public
//    key on ALL target machines, and that's for the root user.
//    this may be tricky to arrange, if not impossible.




public class JsonInstanceCatalog implements InstanceCatalog {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonInstanceCatalog.class);
    
    /** The default location.  Can be overridden. */
    private URL configLocation = null;
    private Map<String, LocalInstance> idToInstanceMap = Maps.newHashMap();

    /** Constructor to build the catalog.
     * @param configFile A file location (relative or absolute), URL, or classpath location.
     * Locations will be tested in that order.  If the a file exists matching the supplied value
     * the catatlog will be read from that file.  If not, a URL will be constructed, validated,
     * and tested for content at the other end.  If that fails, finally an attempt will be made
     * to find a corresponding resource in the classpath.
     */
    public JsonInstanceCatalog(String configFile) {
        Preconditions.checkNotNull(configFile);
        try {
            LOGGER.info("Configuring instance catalog location for " + configFile);
            //check to see if it's a file
            File testFile = new File(configFile);
            if (testFile.exists()) {
                configLocation = testFile.toURI().toURL();
            } else {
                //not a filesystem location, but maybe a URL
                configLocation = parseAndValidateURL(configFile);
            }
            if (configLocation == null) {
                //if we still have nothing, there's a chance that it's on the classpath
                configLocation = ClassLoader.getSystemResource(configFile);
            }
        } catch (IOException e) {
            LOGGER.error("unable convert existing file " + configFile + " to URL", e);
            e.printStackTrace();
        }
        if (configLocation == null) { 
//            this means file did not exist, and value was not resolvable as URL or classpath location
            LOGGER.error(configFile + " did not resolve to file, URL, or classpath location");
            throw new RuntimeException("Instance Catalog at " + configFile 
                + "could not be generated.  Specify using property simianarmy.client.local.catalog.location" );
        }
    }
    
    /** Constructs and validates a URL from a String.
     * @param proposedURL The URL to try
     * @return A URL with content at the other end, or null
     */
    private URL parseAndValidateURL(String proposedURL) {
        try {
            URL retval = new URL(proposedURL);
            //now test it out
            retval.openConnection().connect();
            return retval;
        } catch (MalformedURLException e) {
            LOGGER.error("invalid catalog URL " + proposedURL, e);
            return null;
        } catch (IOException e) {
            LOGGER.error("URL " + proposedURL + " parsed, but could not connect", e);
            return null;
        }
    }
    
    public List<LocalInstanceGroup> localInstanceGroups() {
        ObjectMapper mapper = new ObjectMapper();
        List<LocalInstanceGroup> localGroups = null;
        try {
            LOGGER.info(this.getClass().getName() + " reading catalog data at " + configLocation);
            localGroups = mapper.readValue(configLocation, new TypeReference<List<LocalInstanceGroup>>() { });
        } catch (JsonMappingException e) {
            LOGGER.error("Could not map JSON from " + configLocation, e);
        } catch (JsonParseException e) {
            LOGGER.error("Could not parse JSON at " + configLocation, e);
        } catch (IOException e) {
            LOGGER.error("IOException reading JSON from " + configLocation, e);
        }
        return localGroups;
    }

    @Override
    public List<InstanceGroup> instanceGroups() {
        List<LocalInstanceGroup> localGroups = localInstanceGroups();
        List<InstanceGroup> instanceGroups = Lists.newArrayList();
        if (localGroups == null) {
            return instanceGroups;
        }
        
        idToInstanceMap.clear();//TODO: think about if we need to synchronize this collection.  probably not
        for (LocalInstanceGroup localGroup : localGroups) {
            LOGGER.debug("parsing local instance group "
                + localGroup.getId() + " with " + localGroup.getInstances().size() + " instances");
            BasicInstanceGroup group = new BasicInstanceGroup(
                localGroup.getId(), LocalChaosCrawler.Types.LOCAL, null);
            instanceGroups.add(group);
            for (LocalInstance localInstance : localGroup.getInstances()) {
                LOGGER.debug("parsing local instance " + localInstance.getId()
                    + " at " + localInstance.getHostName());
                idToInstanceMap.put(localInstance.getId(), localInstance);
                group.addInstance(localInstance.getId());
            }
        }
        return instanceGroups;
    }

    @Override
    public LocalInstance getLocalInstanceFromId(String id) {
        return idToInstanceMap.get(id);
    }

}
