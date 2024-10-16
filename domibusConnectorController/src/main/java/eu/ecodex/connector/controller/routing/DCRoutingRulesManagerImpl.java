/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

import eu.ecodex.connector.common.service.ConfigurationPropertyManagerService;
import eu.ecodex.connector.domain.enums.ConfigurationSource;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.persistence.service.DCRoutingRulePersistenceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Holds all Routing Rules in Memory for the different UseCaseDomains.
 */
@Service
public class DCRoutingRulesManagerImpl implements DCRoutingRulesManager {
    private static final Logger LOGGER = LogManager.getLogger(DCRoutingRulesManagerImpl.class);
    private final ConfigurationPropertyManagerService propertyManager;
    private final DCRoutingRulePersistenceService dcRoutingRulePersistenceService;
    /**
     * holds all routing rules which have been added dynamically.
     */
    private final Map<DomibusConnectorBusinessDomain.BusinessDomainId, RoutingConfig> routingRuleMap
        = new HashMap<>();

    public DCRoutingRulesManagerImpl(
        ConfigurationPropertyManagerService propertyManager,
        DCRoutingRulePersistenceService dcRoutingRulePersistenceService) {
        this.propertyManager = propertyManager;
        this.dcRoutingRulePersistenceService = dcRoutingRulePersistenceService;
    }

    @Override
    public synchronized void addBackendRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule routingRule) {
        RoutingConfig rc = routingRuleMap.get(businessDomainId);
        if (rc == null) {
            rc = new RoutingConfig();
            routingRuleMap.put(businessDomainId, rc);
        }

        rc.backendRoutingRules.put(routingRule.getRoutingRuleId(), routingRule);
        LOGGER.debug("Added routing rule [{}]", routingRule);
    }

    @Override
    public RoutingRule persistBackendRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule routingRule) {
        dcRoutingRulePersistenceService.createRoutingRule(businessDomainId, routingRule);
        addBackendRoutingRule(businessDomainId, routingRule);
        routingRule.setConfigurationSource(ConfigurationSource.DB);
        return routingRule;
    }

    @Override
    public void deleteBackendRoutingRuleFromPersistence(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String routingRuleId) {
        dcRoutingRulePersistenceService.deleteRoutingRule(businessDomainId, routingRuleId);
        deleteBackendRoutingRule(businessDomainId, routingRuleId);
    }

    @Override
    public synchronized void deleteBackendRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String routingRuleId) {
        RoutingConfig rc = routingRuleMap.get(businessDomainId);
        if (rc == null) {
            rc = new RoutingConfig();
            routingRuleMap.put(businessDomainId, rc);
        }

        RoutingRule remove = rc.backendRoutingRules.remove(routingRuleId);
        LOGGER.debug("Removed routing rule [{}]", remove);
    }

    @Override
    public Map<String, RoutingRule> getBackendRoutingRules(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        var dcMessageRoutingConfigurationProperties =
            getMessageRoutingConfigurationProperties(businessDomainId);
        return dcMessageRoutingConfigurationProperties.backendRoutingRules;
    }

    @Override
    public String getDefaultBackendName(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        return getMessageRoutingConfigurationProperties(
            businessDomainId).defaultBackendLink.getLinkName();
    }

    @Override
    public void setDefaultBackendName(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String backendName) {
        dcRoutingRulePersistenceService.setDefaultBackendName(businessDomainId, backendName);
    }

    @Override
    public boolean isBackendRoutingEnabled(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        return true; // always true
    }

    private synchronized RoutingConfig getMessageRoutingConfigurationProperties(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        DCMessageRoutingConfigurationProperties routProps =
            propertyManager.loadConfiguration(
                businessDomainId,
                DCMessageRoutingConfigurationProperties.class
            );

        var routingConfig = new RoutingConfig();

        routingConfig.defaultBackendLink =
            new DomibusConnectorLinkPartner.LinkPartnerName(routProps.getDefaultBackendName());
        routingConfig.routingEnabled = true;

        routingConfig.backendRoutingRules.putAll(routProps.getBackendRules());

        RoutingConfig rc = routingRuleMap.getOrDefault(businessDomainId, new RoutingConfig());
        routingConfig.backendRoutingRules.putAll(rc.backendRoutingRules);

        // load db rules, and override existing rules.
        List<RoutingRule> allRoutingRules =
            dcRoutingRulePersistenceService.getAllRoutingRules(businessDomainId);
        Map<String, RoutingRule> collect = allRoutingRules.stream()
            .peek(r -> r.setConfigurationSource(ConfigurationSource.DB))
            .collect(Collectors.toMap(RoutingRule::getRoutingRuleId, Function.identity()));
        routingConfig.backendRoutingRules.putAll(collect);

        return routingConfig;
    }

    private static class RoutingConfig {
        private final Map<String, RoutingRule> backendRoutingRules = new HashMap<>();
        private DomibusConnectorLinkPartner.LinkPartnerName defaultBackendLink;
        private boolean routingEnabled;
    }
}
