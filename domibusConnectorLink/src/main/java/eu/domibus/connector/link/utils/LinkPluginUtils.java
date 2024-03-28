package eu.domibus.connector.link.utils;

import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.*;
import java.util.stream.Collectors;


public class LinkPluginUtils {
    private static final Logger LOGGER = LogManager.getLogger(LinkPluginUtils.class);

    public static ChildContextBuilder getChildContextBuilder(ConfigurableApplicationContext ctx) {
        return new ChildContextBuilder(ctx);
    }

    public static class ChildContextBuilder {
        Map<String, Object> addedSingeltons = new HashMap<>();
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

        public ChildContextBuilder addSingelton(Object bean) {
            this.addedSingeltons.put(bean.getClass().getName(), bean);
            return this;
        }

        public ChildContextBuilder addSingelton(String name, Object bean) {
            this.addedSingeltons.put(name, bean);
            return this;
        }

        public ChildContextBuilder withSources(Class<?>... sources) {
            builder.sources(sources);
            return this;
        }

        public ChildContextBuilder withDomibusConnectorLinkConfiguration(DomibusConnectorLinkConfiguration linkConfig) {
            builder.properties(new HashMap<>(linkConfig.getProperties()));
            return this.addSingelton("linkConfig", linkConfig);
        }

        public ChildContextBuilder withDomibusConnectorLinkPartner(DomibusConnectorLinkPartner linkPartner) {
            builder.properties(mapProps(linkPartner.getProperties()));
            return this.addSingelton("linkPartner", linkPartner);
        }

        private Map<String, Object> mapProps(Map<String, String> properties) {
            return properties.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        public ChildContextBuilder withProfiles(String... profiles) {
            this.profiles.addAll(Arrays.asList(profiles));
            return this;
        }

        public ChildContextBuilder withProperties(Properties properties) {
            // builder.properties(properties);
            props.putAll(properties);
            return this;
        }

        public ConfigurableApplicationContext run(String... args) {
            builder.initializers(applicationContext -> addedSingeltons
                    .entrySet().forEach(entry -> {
                        LOGGER.trace("Adding singelton with name [{}] as bean [{}]", entry.getKey(), entry.getValue());
                        applicationContext.getBeanFactory().registerSingleton(entry.getKey(), entry.getValue());
                    }));

            builder.properties(props);
            builder.profiles(profiles.toArray(new String[]{}));
            LOGGER.trace("Running child context with " + "\n\tproperties [{}]" + "\n\tprofiles [{}]", props, profiles);

            try {
                return builder.run(args);
            } catch (Exception e) {
                throw new LinkPluginException("cannot start link plugin context", e);
            }
        }
    }
}
