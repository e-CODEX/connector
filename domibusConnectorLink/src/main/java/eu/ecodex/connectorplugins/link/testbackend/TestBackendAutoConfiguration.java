/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.testbackend;

import eu.ecodex.connector.c2ctests.config.ConnectorTestConfigurationProperties;
import eu.ecodex.connector.common.DomibusConnectorDefaults;
import eu.ecodex.connector.common.service.ConfigurationPropertyManagerService;
import eu.ecodex.connector.common.service.DCBusinessDomainManager;
import eu.ecodex.connector.controller.routing.DCRoutingRulesManager;
import eu.ecodex.connector.controller.routing.RoutingRule;
import eu.ecodex.connector.controller.routing.RoutingRulePattern;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.link.service.DCActiveLinkManagerService;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Initializes and registers a test backend.
 */
//@Profile("plugin-" + TestbackendPlugin.IMPL_NAME)
@Configuration
@ComponentScan(basePackageClasses = TestBackendAutoConfiguration.class)
@ConditionalOnProperty(
    prefix = "connector.link.plugins." + "plugin-" + TestbackendPlugin.IMPL_NAME, value = "enabled",
    havingValue = "true", matchIfMissing = true
)
@ConditionalOnBean(ConfigurationPropertyManagerService.class)
public class TestBackendAutoConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(TestBackendAutoConfiguration.class);
    public static final String IMPL_NAME = "testbackendplugin";
    private final DCActiveLinkManagerService dcActiveLinkManagerService;
    private final DCRoutingRulesManager routingRulesManager;
    private final DCBusinessDomainManager businessDomainManager;
    private final ConfigurationPropertyManagerService configurationPropertyLoaderService;

    /**
     * Instantiates a new TestBackendAutoConfiguration object.
     *
     * @param dcActiveLinkManagerService         the DCActiveLinkManagerService instance
     * @param routingRulesManager                the DCRoutingRulesManager instance
     * @param businessDomainManager              the DCBusinessDomainManager instance
     * @param configurationPropertyLoaderService the ConfigurationPropertyManagerService instance
     */
    public TestBackendAutoConfiguration(
        DCActiveLinkManagerService dcActiveLinkManagerService,
        DCRoutingRulesManager routingRulesManager,
        DCBusinessDomainManager businessDomainManager,
        ConfigurationPropertyManagerService configurationPropertyLoaderService) {
        this.dcActiveLinkManagerService = dcActiveLinkManagerService;
        this.routingRulesManager = routingRulesManager;
        this.businessDomainManager = businessDomainManager;
        this.configurationPropertyLoaderService = configurationPropertyLoaderService;
    }

    /**
     * Create and register the test backend and the according routing rules for it.
     */
    @PostConstruct
    public void init() {
        boolean b = configureRoutingRules();

        if (b) {
            var linkConfiguration = new DomibusConnectorLinkConfiguration();
            linkConfiguration.setConfigurationSource(ConfigurationSource.IMPL);
            linkConfiguration.setConfigName(
                new DomibusConnectorLinkConfiguration.LinkConfigName("TestBackendConfig"));
            linkConfiguration.setLinkImpl(TestbackendPlugin.IMPL_NAME);

            var domibusConnectorLinkPartner = new DomibusConnectorLinkPartner();
            domibusConnectorLinkPartner.setEnabled(true);
            domibusConnectorLinkPartner.setDescription("Default Test Backend Link Partner");
            domibusConnectorLinkPartner.setLinkType(LinkType.BACKEND);
            domibusConnectorLinkPartner.setConfigurationSource(ConfigurationSource.IMPL);
            domibusConnectorLinkPartner.setLinkPartnerName(
                new DomibusConnectorLinkPartner.LinkPartnerName(
                    DomibusConnectorDefaults.DEFAULT_TEST_BACKEND));
            domibusConnectorLinkPartner.setLinkConfiguration(linkConfiguration);

            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Connector2Connector test plugin is enabled. Because profile [{}] is "
                    + "active and in at leas one business domain config connector2connector tests "
                    + "are enabled. Connector Test Backend will be activated!",
                "plugin-" + TestbackendPlugin.IMPL_NAME
            );
            dcActiveLinkManagerService.activateLinkPartner(domibusConnectorLinkPartner);
        }
    }

    /**
     * Creates and registers the routing rules for the testbackend.
     *
     * @return true if in any business domain test backend is enabled
     */
    private boolean configureRoutingRules() {
        var enabled = false;
        List<DomibusConnectorBusinessDomain.BusinessDomainId> activeBusinessDomainIds =
            businessDomainManager.getActiveBusinessDomainIds();
        for (DomibusConnectorBusinessDomain.BusinessDomainId laneId : activeBusinessDomainIds) {
            ConnectorTestConfigurationProperties c2cTestProperties =
                configurationPropertyLoaderService.loadConfiguration(
                    laneId,
                    ConnectorTestConfigurationProperties.class
                );
            enabled = enabled || c2cTestProperties.isEnabled();
            if (c2cTestProperties.isEnabled()) {
                // create and add rule for this businessDomain
                var routingRule = new RoutingRule();
                routingRule.setConfigurationSource(ConfigurationSource.IMPL);
                routingRule.setPriority(
                    RoutingRule.HIGH_PRIORITY); // add routing rule with high priority
                routingRule.setLinkName(DomibusConnectorDefaults.DEFAULT_TEST_BACKEND);

                var rule = "";
                if (c2cTestProperties.getAction() != null) {
                    rule = concatAnd(rule, String.format(
                        "equals(Action, '%s')",
                        c2cTestProperties.getAction().getAction()
                    ));
                }
                if (c2cTestProperties.getService() != null) {
                    if (StringUtils.hasText(c2cTestProperties.getService().getName())) {
                        rule = concatAnd(rule, String.format(
                            "equals(ServiceName, '%s')",
                            c2cTestProperties.getService()
                                             .getName()
                        ));
                    }
                    if (StringUtils.hasText(c2cTestProperties.getService().getServiceType())) {
                        rule = concatAnd(rule, String.format(
                            "equals(ServiceType, '%s')",
                            c2cTestProperties.getService()
                                             .getServiceType()
                        ));
                    }
                }
                if (!StringUtils.hasText(rule)) {
                    var error = String.format(
                        "Cannot create RoutingRule for ConnectorTests. Check Config of "
                            + "BusinessDomain %s", laneId
                    );
                    throw new IllegalArgumentException(error);
                }
                try {
                    var routingRulePattern = new RoutingRulePattern(rule);
                    routingRule.setDescription(
                        "Automatically by Connector2Connector Test created routing rule. To route "
                            + "connector tests messages to testbackend"
                    );
                    routingRule.setMatchClause(routingRulePattern);
                    LOGGER.info(
                        LoggingMarker.Log4jMarker.CONFIG,
                        "Adding the routing rule {} to backend routing rules for "
                            + "testbackend on business domain [{}]",
                        routingRule, laneId
                    );
                    routingRulesManager.addBackendRoutingRule(laneId, routingRule);
                } catch (Exception e) {
                    var error = String.format(
                        "Cannot create RoutingRule for ConnectorTests. The routing rule %s is "
                            + "illegal. Check Config of BusinessDomain %s", rule, laneId
                    );
                    throw new IllegalArgumentException(error);
                }
            }
        }
        return enabled;
    }

    private String concatAnd(String rule, String ruleToAppend) {
        if (StringUtils.hasText(rule)) {
            return String.format("&(%s,%s)", rule, ruleToAppend);
        }
        return ruleToAppend;
    }
}
