package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.annotation.MultipartConfig;


@EnableTransactionManagement
@Configuration
@EnableWebMvc
@EnableVaadin("eu.domibus.connector.ui")
@MultipartConfig
public class DomibusConnectorVaadinWebContext {
}
