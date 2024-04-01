package eu.domibus.connector.ui.view.areas.pmodes;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.StaticContentView;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@UIScope
@Component
@Route(value = PmodeOverview.ROUTE, layout = PmodeLayout.class)
@RoleRequired(role = "ADMIN")
@Order(3)
@TabMetadata(title = "Information on PMode-Sets", tabGroup = PmodeLayout.TAB_GROUP_NAME)
public class PmodeOverview extends StaticContentView {
    public static final String ROUTE = "information";

    public PmodeOverview() {
        super("documentation/ui/pmodes/pmodes_overview.html");
    }
}
