/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.controller.routing.RoutingRule;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import eu.domibus.connector.persistence.service.DCRoutingRulePersistenceService;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * This service uses the DC_MESSAGE_LANE and DC_MESSAGE_LANE_PROPERTY tables to store the
 * RoutingRules This tables are also used by the PropertyLoading Services.
 * TODO: Maybe this should be seperated into it's own table!
 */

@SuppressWarnings("squid:S1135")
@Service
@Transactional
public class DCRoutingRulePersistenceServiceImpl implements DCRoutingRulePersistenceService {
    // TODO: avoid this by using DCMessageRoutingConfigurationProperties
    private static final String DEFAULT_BACKEND_NAME_PROPERTY_NAME =
        "connector.routing.default-backend-name";
    private final ConfigurationPropertyManagerService configurationPropertyManagerService;
    private final DomibusConnectorBusinessDomainDao businessDomainDao;
    private final BeanToPropertyMapConverter beanToPropertyMapConverter;

    /**
     * Implementation class for the DCRoutingRulePersistenceService interface.
     */
    public DCRoutingRulePersistenceServiceImpl(
        ConfigurationPropertyManagerService configurationPropertyManagerService,
        DomibusConnectorBusinessDomainDao businessDomainDao,
        BeanToPropertyMapConverter beanToPropertyMapConverter) {
        this.configurationPropertyManagerService = configurationPropertyManagerService;
        this.businessDomainDao = businessDomainDao;
        this.beanToPropertyMapConverter = beanToPropertyMapConverter;
    }

    @Override
    public void createRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule rr) {
        rr.setConfigurationSource(ConfigurationSource.DB);
        var messageLane = getMessageLane(businessDomainId);
        Map<String, String> properties = messageLane.getProperties();
        Map<String, String> stringStringMap =
            beanToPropertyMapConverter.readBeanPropertiesToMap(rr, "");
        stringStringMap.forEach((key, value) -> properties.put(
            PREFIX + rr.getRoutingRuleId() + "]" + key, value
        ));
        businessDomainDao.save(messageLane);
    }

    private PDomibusConnectorMessageLane getMessageLane(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        Optional<PDomibusConnectorMessageLane> byName =
            businessDomainDao.findByName(businessDomainId);
        if (byName.isPresent()) {
            return byName.get();
        } else {
            throw new IllegalArgumentException(
                "The business domain with id [" + businessDomainId + "] does not exist in DB!");
        }
    }

    @Override
    public void deleteRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String ruleId) {
        PDomibusConnectorMessageLane messageLane = getMessageLane(businessDomainId);
        List<String> keys = getAllKeysForRoutingRule(ruleId, messageLane);

        // remove all entries key belongs to routing rule with given ruleId
        keys.forEach(k -> messageLane.getProperties().remove(k));

        businessDomainDao.save(messageLane);
    }

    private List<String> getAllKeysForRoutingRule(
        String ruleId, PDomibusConnectorMessageLane messageLane) {
        String prefix = getRoutingRuleKey(ruleId);

        return messageLane.getProperties().keySet().stream()
                          .filter(k -> k.startsWith(prefix))
                          .toList();
    }

    @Override
    public void updateRoutingRule(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, RoutingRule rr) {
        createRoutingRule(businessDomainId, rr);
    }

    @Override
    public List<RoutingRule> getAllRoutingRules(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        PDomibusConnectorMessageLane messageLane = getMessageLane(businessDomainId);
        Map<String, String> properties = messageLane.getProperties();
        Set<String> routingRuleIds = properties.keySet()
                                               .stream()
                                               .filter(k -> k.length() > PREFIX.length())
                                               .map(k -> k.substring(PREFIX.length()))
                                               .filter(k -> k.contains("]"))
                                               .map(k -> k.substring(0, k.indexOf("]")))
                                               .collect(Collectors.toSet());

        Set<RoutingRule> routingRules = new HashSet<>();
        for (String ruleId : routingRuleIds) {
            String routingRuleKey = getRoutingRuleKey(ruleId);
            RoutingRule routingRule =
                configurationPropertyManagerService.loadConfigurationOnlyFromMap(
                    properties,
                    RoutingRule.class,
                    routingRuleKey
                );
            routingRule.setConfigurationSource(ConfigurationSource.DB);
            routingRules.add(routingRule);
        }

        return new ArrayList<>(routingRules);
    }

    @Override
    public void setDefaultBackendName(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId, String backendName) {
        var messageLane = getMessageLane(businessDomainId);
        Map<String, String> properties = messageLane.getProperties();
        properties.put(DEFAULT_BACKEND_NAME_PROPERTY_NAME, backendName);
        businessDomainDao.save(messageLane);
    }

    public static final String PREFIX = "connector.routing.backend-rules[";

    private String getRoutingRuleKey(String ruleId) {
        // better us Constant here...
        return PREFIX + ruleId + "]";
    }
}
