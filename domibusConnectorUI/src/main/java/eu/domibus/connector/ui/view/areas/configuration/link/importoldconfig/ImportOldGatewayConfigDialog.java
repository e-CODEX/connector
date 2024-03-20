package eu.domibus.connector.ui.view.areas.configuration.link.importoldconfig;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkPartnerField;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.link.utils.Connector42LinkConfigTo43LinkConfigConverter;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportOldGatewayConfigDialog extends ImportOldConfigDialog {

    public ImportOldGatewayConfigDialog(ObjectProvider<DCLinkConfigurationField> linkConfigurationFieldObjectProvider,
                                        ObjectProvider<DCLinkPartnerField> linkPartnerFieldObjectProvider,
                                        BeanToPropertyMapConverter beanToPropertyMapConverter,
                                        DCLinkFacade dcLinkFacade,
                                        JdbcTemplate jdbcTemplate
                                        ) {
        super(linkConfigurationFieldObjectProvider, linkPartnerFieldObjectProvider, beanToPropertyMapConverter, dcLinkFacade, jdbcTemplate);
    }

    @Override
    protected List<DomibusConnectorLinkPartner> getLinkPartners(Connector42LinkConfigTo43LinkConfigConverter connector42LinkConfigTo43LinkConfigConverter) {
        return connector42LinkConfigTo43LinkConfigConverter.getGwPartner();
    }

}
