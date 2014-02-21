/*
 *
 *  Copyright 2012 Netflix, Inc.
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
package com.netflix.simianarmy.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.simianarmy.MonkeyRunner;

/**
 * Will periodically run the configured monkeys.
 */
@SuppressWarnings("serial")
public class BasicMonkeyServer extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicMonkeyServer.class);

    private static final MonkeyRunner RUNNER = MonkeyRunner.getInstance();

    /**
     * Add the monkeys that will be run.
     */
    @SuppressWarnings("unchecked")
    public void addMonkeysToRun() {
        LOGGER.info("Adding Chaos Monkey.");
        RUNNER.replaceMonkey(this.chaosClass, this.chaosContextClass);

        if (this.volumeTaggingClass != null) {
            LOGGER.info("Adding Volume Tagging Monkey.");
            RUNNER.replaceMonkey(this.volumeTaggingClass, this.volumeTaggingContextClass);
        } else {
            LOGGER.info("Skipping Volume Tagging Monkey.");
        }

        if (this.conformityClass != null) {
            LOGGER.info("Adding Conformity Monkey.");
            RUNNER.replaceMonkey(this.conformityClass, this.conformityContextClass);
        } else {
            LOGGER.info("Skipping Conformity Monkey.");
        }

        if (this.janitorClass != null) {
            LOGGER.info("Adding Janitor Monkey.");
            RUNNER.replaceMonkey(this.janitorClass, this.janitorContextClass);
        } else {
            LOGGER.info("Skipping Janitor Monkey.");
        }
    }

    /**
     * make the class of the chaos context object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class chaosContextClass = com.netflix.simianarmy.basic.BasicChaosMonkeyContext.class;

    /**
     * make the class of the chaos object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class chaosClass = com.netflix.simianarmy.basic.chaos.BasicChaosMonkey.class;

    /**
     * make the class of the conformity context object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class conformityContextClass = null;
    // default is com.netflix.simianarmy.basic.conformity.BasicConformityMonkeyContext.class;

    /**
     * make the class of the conformity object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class conformityClass = null;
    // default is com.netflix.simianarmy.basic.conformity.BasicConformityMonkey.class;

    /**
     * make the class of the janitor context object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class janitorContextClass = null;
    // default is com.netflix.simianarmy.basic.janitor.BasicJanitorMonkeyContext.class;

    /**
     * make the class of the janitor object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class janitorClass = null;
    // default is com.netflix.simianarmy.basic.janitor.BasicJanitorMonkey.class;

    /**
     * make the class of the volume tagging context object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class volumeTaggingContextClass = null;
    // default is com.netflix.simianarmy.basic.janitor.BasicVolumeTaggingMonkeyContext.class;

    /**
     * make the class of the volume tagging object configurable.
     */
    @SuppressWarnings("rawtypes")
    private Class volumeTaggingClass = null;
    // default is com.netflix.simianarmy.aws.janitor.VolumeTaggingMonkey.class;

    @Override
    public void init() throws ServletException {
        super.init();
        configureClient();
        addMonkeysToRun();
        RUNNER.start();
    }

    /**
     * Loads the client that is configured.
     *
     * @throws ServletException
     *             if the configured client cannot be loaded properly
     */
    @SuppressWarnings("rawtypes")
    private void configureClient() throws ServletException {
        Properties clientConfig = loadClientConfigProperties();

        Class newContextClass = loadClientClass(clientConfig, "simianarmy.client.context.class");
        this.chaosContextClass = (newContextClass == null ? this.chaosContextClass : newContextClass);

        Class newChaosClass = loadClientClass(clientConfig, "simianarmy.client.chaos.class");
        this.chaosClass = (newChaosClass == null ? this.chaosClass : newChaosClass);

        this.conformityContextClass = loadClientClass(clientConfig, "simianarmy.client.conformity.context.class");
        this.conformityClass = loadClientClass(clientConfig, "simianarmy.client.conformity.class");

        this.volumeTaggingContextClass = loadClientClass(clientConfig, "simianarmy.client.volumeTagging.context.class");
        this.volumeTaggingClass = loadClientClass(clientConfig, "simianarmy.client.volumeTagging.class");

        this.janitorContextClass = loadClientClass(clientConfig, "simianarmy.client.janitor.context.class");
        this.janitorClass = loadClientClass(clientConfig, "simianarmy.client.janitor.class");
    }

    @SuppressWarnings("rawtypes")
    private Class loadClientClass(Properties clientConfig, String key)  throws ServletException {
        ClassLoader classLoader = BasicMonkeyServer.class.getClassLoader();
        try {
            String clientClassName = clientConfig.getProperty(key);
            if (clientClassName == null || clientClassName.isEmpty()) {
                LOGGER.info("using standard client for " + key);
                return null;
            }
            Class newClass = classLoader.loadClass(clientClassName);
            LOGGER.info("using " + key + " loaded " + newClass.getCanonicalName());
            return newClass;
        } catch (ClassNotFoundException e) {
            throw new ServletException("Could not load " + key, e);
        }
    }

    /**
     * Load the client config properties file.
     *
     * @return Properties The contents of the client config file
     * @throws ServletException
     *             if the file cannot be read
     */
    private Properties loadClientConfigProperties() throws ServletException {
        String propertyFileName = "client.properties";
        String clientConfigFileName = System.getProperty(propertyFileName, "/" + propertyFileName);
        LOGGER.info("using client properties " + clientConfigFileName);

        InputStream input = null;
        Properties p = new Properties();
        try {
            try {
                input = BasicMonkeyServer.class.getResourceAsStream(clientConfigFileName);
                p.load(input);
                return p;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (IOException e) {
            throw new ServletException("Could not load " + clientConfigFileName, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroy() {
        RUNNER.stop();
        LOGGER.info("Stopping Chaos Monkey.");
        RUNNER.removeMonkey(this.chaosClass);
        LOGGER.info("Stopping volume tagging Monkey.");
        RUNNER.removeMonkey(this.volumeTaggingClass);
        LOGGER.info("Stopping Janitor Monkey.");
        RUNNER.removeMonkey(this.janitorClass);
        LOGGER.info("Stopping Conformity Monkey.");
        RUNNER.removeMonkey(this.conformityClass);
        super.destroy();
    }
}
