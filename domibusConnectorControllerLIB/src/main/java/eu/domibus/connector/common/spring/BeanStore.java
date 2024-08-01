/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.spring;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;

class BeanStore {
    private static final Logger LOGGER = LoggerFactory
        .getLogger(BeanStore.class.getName());
    private final Map<String, Object> objects = new HashMap<>();
    private final Map<String, Runnable> destructionCallbacks = new HashMap<>();

    synchronized Object get(String name, ObjectFactory<?> objectFactory) {
        var bean = objects.get(name);
        if (bean == null) {
            bean = objectFactory.getObject();
            objects.put(name, bean);
        }
        return bean;
    }

    synchronized Object remove(String name) {
        destructionCallbacks.remove(name);
        return objects.remove(name);
    }

    synchronized void registerDestructionCallback(String name, Runnable callback) {
        destructionCallbacks.put(name, callback);
    }

    synchronized void destroy() {
        for (Runnable destructionCallback : destructionCallbacks.values()) {
            try {
                destructionCallback.run();
            } catch (Exception e) {
                LOGGER.error("BeanStore destruction callback failed", e);
            }
        }
        destructionCallbacks.clear();
        objects.clear();
    }
}
