package eu.domibus.connector.ui.utils.field;

import com.vaadin.flow.component.customfield.CustomField;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;


@Service
public class FindFieldService {
    private final ApplicationContext applicationContext;

    public FindFieldService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <T> CustomField<T> findField(Class<T> clz) {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(CustomField.class, clz);
        String[] beanNamesForType = applicationContext.getBeanNamesForType(resolvableType);
        if (beanNamesForType.length == 0) {
            throw new IllegalArgumentException("No field found for type " + clz);
        }
        return (CustomField<T>) applicationContext.getBean(beanNamesForType[0]);
    }
}
