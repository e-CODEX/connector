package eu.domibus.connector.common.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;

import java.util.HashMap;
import java.util.Map;


class BeanStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(BeanStore.class.getName());

    private final Map<String, Object> objects = new HashMap<>();
    private final Map<String, Runnable> destructionCallbacks = new HashMap<>();

    synchronized Object get(String name, ObjectFactory<?> objectFactory) {
        // return objectFactory.getObject();
        Object bean = objects.get(name);
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
