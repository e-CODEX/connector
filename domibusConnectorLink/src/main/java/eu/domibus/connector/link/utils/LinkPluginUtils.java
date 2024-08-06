/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.utils;

import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The LinkPluginUtils class provides utility methods for configuring and running a child context in
 * a link plugin.
 */
public class LinkPluginUtils {
    private static final Logger LOGGER = LogManager.getLogger(LinkPluginUtils.class);

    /**
     * The ChildContextBuilder class is responsible for building a child application context. It
     * provides methods to add singleton beans, set application sources, set Domibus connector link
     * configuration, set Domibus connector link partner, set profiles, and set properties. It also
     * allows running the child context and returns the created ConfigurableApplicationContext.
     */
    public static class ChildContextBuilder {
        Map<String, Object> addedSingletons = new HashMap<>();
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        Properties props = new Properties();
        private final List<String> profiles = new ArrayList<>();

        private ChildContextBuilder(ConfigurableApplicationContext parent) {
            // props.put("org.springframework.boot.logging.LoggingSystem", "none");
            // make sure child context loads same logging config
            if (parent.getEnvironment().getProperty("logging.config") != null) {
                props.put("logging.config", parent.getEnvironment().getProperty("logging.config"));
            }

            builder.parent(parent);
            builder.bannerMode(Banner.Mode.OFF);
            builder.web(WebApplicationType.NONE);
        }

        public ChildContextBuilder addSingleton(Object bean) {
            this.addedSingletons.put(bean.getClass().getName(), bean);
            return this;
        }

        public ChildContextBuilder addSingleton(String name, Object bean) {
            this.addedSingletons.put(name, bean);
            return this;
        }

        public ChildContextBuilder withSources(Class<?>... sources) {
            builder.sources(sources);
            return this;
        }

        public ChildContextBuilder withDomibusConnectorLinkConfiguration(
            DomibusConnectorLinkConfiguration linkConfig) {
            builder.properties(new HashMap<>(linkConfig.getProperties()));
            return this.addSingleton("linkConfig", linkConfig);
        }

        public ChildContextBuilder withDomibusConnectorLinkPartner(
            DomibusConnectorLinkPartner linkPartner) {
            builder.properties(mapProps(linkPartner.getProperties()));
            return this.addSingleton("linkPartner", linkPartner);
        }

        private Map<String, Object> mapProps(Map<String, String> properties) {
            return properties
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public ChildContextBuilder withProfiles(String... profiles) {
            this.profiles.addAll(Arrays.asList(profiles));
            return this;
        }

        /**
         * Adds properties to the ChildContextBuilder.
         */
        public ChildContextBuilder withProperties(Properties properties) {
            // builder.properties(properties);
            props.putAll(properties);
            return this;
        }

        /**
         * Runs the child context with the given arguments.
         *
         * @param args the arguments passed to the child context
         * @return the created ConfigurableApplicationContext
         * @throws LinkPluginException if the child context cannot be started
         */
        public ConfigurableApplicationContext run(String... args) {
            builder.initializers(
                applicationContext -> addedSingletons
                    .entrySet()
                    .forEach(
                        entry -> {
                            LOGGER.trace(
                                "Adding singleton with name [{}] as bean [{}]",
                                entry.getKey(),
                                entry.getValue()
                            );
                            applicationContext
                                .getBeanFactory()
                                .registerSingleton(
                                    entry.getKey(),
                                    entry.getValue()
                                );
                        }));

            builder.properties(props);
            builder.profiles(profiles.toArray(new String[] {}));
            LOGGER.trace(
                "Running child context with \n\tproperties [{}]\n\tprofiles [{}]",
                props,
                profiles
            );

            try {
                return builder.run(args);
            } catch (Exception e) {
                throw new LinkPluginException("cannot start link plugin context", e);
            }
        }
    }

    public static ChildContextBuilder getChildContextBuilder(ConfigurableApplicationContext ctx) {
        return new ChildContextBuilder(ctx);
    }
}
