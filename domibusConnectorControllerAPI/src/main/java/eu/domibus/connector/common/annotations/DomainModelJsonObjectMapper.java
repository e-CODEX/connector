package eu.domibus.connector.common.annotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Qualifier(DomainModelJsonObjectMapper.VALUE)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainModelJsonObjectMapper {

    public static final String VALUE = "eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper";

}
