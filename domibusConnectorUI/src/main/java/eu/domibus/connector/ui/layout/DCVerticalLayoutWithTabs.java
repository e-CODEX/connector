package eu.domibus.connector.ui.layout;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;

import eu.domibus.connector.ui.utils.DCTabHandler;
import eu.domibus.connector.ui.view.areas.pmodes.PmodeLayout;

public class DCVerticalLayoutWithTabs extends VerticalLayout implements BeforeEnterObserver, RouterLayout {

	protected final static Logger LOGGER = LoggerFactory.getLogger(PmodeLayout.class);

    public final String TAB_GROUP_NAME;
    private final ApplicationContext applicationContext;

    private DCTabHandler DCTabHandler = new DCTabHandler();
	
	/**
	 * @param tAB_GROUP_NAME
	 * @param applicationContext
	 */
	public DCVerticalLayoutWithTabs(String tAB_GROUP_NAME, ApplicationContext applicationContext) {
		super();
		TAB_GROUP_NAME = tAB_GROUP_NAME;
		this.applicationContext = applicationContext;
	}

	@PostConstruct
    void init() {
    	
    	setSizeFull();
    	
        DCTabHandler.createTabs(applicationContext, TAB_GROUP_NAME);
        add(DCTabHandler.getTabs());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        DCTabHandler.beforeEnter(event);
    }

}
