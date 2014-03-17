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
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.netflix.simianarmy.Monkey;
import com.netflix.simianarmy.Monkey.Context;
import com.netflix.simianarmy.MonkeyRunner;
import com.netflix.simianarmy.MonkeyType;
import com.netflix.simianarmy.basic.chaos.BasicChaosMonkey;
import com.netflix.simianarmy.janitor.JanitorMonkey;

import static com.netflix.simianarmy.chaos.ChaosMonkey.Type.CHAOS;
import static com.netflix.simianarmy.janitor.JanitorMonkey.Type.JANITOR;
import static com.netflix.simianarmy.aws.janitor.VolumeTaggingMonkey.Type.VOLUME_TAGGING;
import static com.netflix.simianarmy.conformity.ConformityMonkey.Type.CONFORMITY;

/**
 * Will periodically run the configured monkeys.
 */
@SuppressWarnings("serial")
public class BasicMonkeyServer extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicMonkeyServer.class);

    private static final MonkeyRunner RUNNER = MonkeyRunner.getInstance();
    private static final Map<MonkeyType, Class<? extends Monkey>> MONKEY_IMPL_MAP = Maps.newHashMap();
    private static final Map<MonkeyType, Class<? extends Context>> MONKEY_CONTEXT_IMPL_MAP = Maps.newHashMap();

    /**
     * Add the monkeys that will be run.
     */
    public void addMonkeysToRun() {
        addMonkeyToRun(CHAOS, "Chaos");
        addMonkeyToRun(VOLUME_TAGGING, "Volume Tagging");
        addMonkeyToRun(CONFORMITY, "Conformity");
        addMonkeyToRun(JANITOR, "Janitor");
    }

    private void addMonkeyToRun(MonkeyType type, String name) {
        Class<? extends Monkey> monkeyClass = MONKEY_IMPL_MAP.get(type);
        if (monkeyClass != null) {
            LOGGER.info("Adding " + name + " Monkey.");
            RUNNER.replaceMonkey(monkeyClass, MONKEY_CONTEXT_IMPL_MAP.get(type));
        } else {
            LOGGER.info("Skipping " + name + " Monkey.");
        }
    }

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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void configureClient() throws ServletException {
        Properties clientConfig = loadClientConfigProperties();

        Class newContextClass = loadClientClass(clientConfig, "simianarmy.client.context.class");
        MONKEY_CONTEXT_IMPL_MAP.put(CHAOS, newContextClass == null ? BasicChaosMonkeyContext.class : newContextClass);

        Class newChaosClass = loadClientClass(clientConfig, "simianarmy.client.chaos.class");
        MONKEY_IMPL_MAP.put(CHAOS, newChaosClass == null ? BasicChaosMonkey.class : newChaosClass);

        Class conformityContextClass = loadClientClass(clientConfig, "simianarmy.client.conformity.context.class");
        MONKEY_CONTEXT_IMPL_MAP.put(CONFORMITY, conformityContextClass);
        Class conformityClass = loadClientClass(clientConfig, "simianarmy.client.conformity.class");
        MONKEY_IMPL_MAP.put(CONFORMITY, conformityClass);

        Class volumeTaggingContextClass =
            loadClientClass(clientConfig, "simianarmy.client.volumeTagging.context.class");
        MONKEY_CONTEXT_IMPL_MAP.put(VOLUME_TAGGING, volumeTaggingContextClass);
        Class volumeTaggingClass = loadClientClass(clientConfig, "simianarmy.client.volumeTagging.class");
        MONKEY_IMPL_MAP.put(VOLUME_TAGGING, volumeTaggingClass);

        Class janitorContextClass = loadClientClass(clientConfig, "simianarmy.client.janitor.context.class");
        MONKEY_CONTEXT_IMPL_MAP.put(JanitorMonkey.Type.JANITOR, janitorContextClass);
        Class janitorClass = loadClientClass(clientConfig, "simianarmy.client.janitor.class");
        MONKEY_IMPL_MAP.put(JanitorMonkey.Type.JANITOR, janitorClass);

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

    @Override
    public void destroy() {
        RUNNER.stop();
        LOGGER.info("Stopping Chaos Monkey.");
        RUNNER.removeMonkey(MONKEY_IMPL_MAP.get(CHAOS));
        LOGGER.info("Stopping volume tagging Monkey.");
        RUNNER.removeMonkey(MONKEY_IMPL_MAP.get(VOLUME_TAGGING));
        LOGGER.info("Stopping Janitor Monkey.");
        RUNNER.removeMonkey(MONKEY_IMPL_MAP.get(JanitorMonkey.Type.JANITOR));
        LOGGER.info("Stopping Conformity Monkey.");
        RUNNER.removeMonkey(MONKEY_IMPL_MAP.get(CONFORMITY));
        super.destroy();
    }
}
