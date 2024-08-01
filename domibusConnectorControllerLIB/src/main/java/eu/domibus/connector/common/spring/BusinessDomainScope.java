/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.spring;

import eu.domibus.connector.common.service.BusinessDomainConfigurationChange;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationListener;

/**
 * The BusinessDomainScope class implements the Scope interface and serves as a business domain
 * scope for managing beans within a specific business domain. It also listens to
 * BusinessDomainConfigurationChange events to handle the destruction of beans associated with a
 * specific business domain when its configuration changes.
 */
public class BusinessDomainScope
    implements Scope, ApplicationListener<BusinessDomainConfigurationChange> {
    private final Map<DomibusConnectorBusinessDomain.BusinessDomainId, BeanStore>
        businessDomainToBeanStore = new HashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        var beanStore = getBeanStore();
        return beanStore.get(name, objectFactory);
    }

    @Override
    public Object remove(String name) {
        return getBeanStore().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        getBeanStore().registerDestructionCallback(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return CurrentBusinessDomain.getCurrentBusinessDomain();
    }

    @Override
    public String getConversationId() {
        return null;
    }

    private synchronized BeanStore getBeanStore() {
        DomibusConnectorBusinessDomain.BusinessDomainId currentBusinessDomain =
            CurrentBusinessDomain.getCurrentBusinessDomain();
        if (currentBusinessDomain == null) {
            throw new IllegalStateException("There is currently no business domain scope active!");
        }
        var beanStore = businessDomainToBeanStore.get(currentBusinessDomain);
        if (beanStore == null) {
            beanStore = new BeanStore();
            businessDomainToBeanStore.put(currentBusinessDomain, beanStore);
        }
        return beanStore;
    }

    @Override
    public void onApplicationEvent(BusinessDomainConfigurationChange event) {
        var businessDomainId = event.getBusinessDomainId();
        destroyBeanStoreForBusinessScope(businessDomainId);
    }

    private synchronized void destroyBeanStoreForBusinessScope(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        var beanStore = businessDomainToBeanStore.get(businessDomainId);
        if (beanStore != null) {
            beanStore.destroy();
            businessDomainToBeanStore.remove(businessDomainId);
        }
    }
}
