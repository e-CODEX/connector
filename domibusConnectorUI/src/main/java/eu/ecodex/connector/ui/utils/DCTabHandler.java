/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import eu.ecodex.connector.ui.configuration.SecurityUtils;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * Helper class for creating a tab menu with support to navigate between these tabs and also only
 * show tabs which the corresponding view can be accessed as enabled.
 */
@Data
public class DCTabHandler implements BeforeEnterObserver {
    private static final Logger LOGGER = LogManager.getLogger(DCTabHandler.class);
    private Tabs tabMenu = new Tabs();
    private Map<Tab, Class> tabsToPages = new HashMap<>();
    private Map<Class, Tab> pagesToTab = new HashMap<>();
    private String tabFontSize = "normal";

    public DCTabHandler() {
        tabMenu.addSelectedChangeListener(this::tabSelectionChanged);
    }

    public Tabs getTabs() {
        return this.tabMenu;
    }

    public TabBuilder createTab() {
        return new TabBuilder();
    }

    private void tabSelectionChanged(Tabs.SelectedChangeEvent selectedChangeEvent) {
        if (selectedChangeEvent.isFromClient()) {
            var selectedTab = selectedChangeEvent.getSelectedTab();
            var componentClazz = tabsToPages.get(selectedTab);
            LOGGER.debug("Navigate to [{}]", componentClazz);
            UI.getCurrent().navigate(componentClazz);
        }
    }

    /**
     * Sets the current selected tab dependent on the current view.
     */
    private void setSelectedTab(BeforeEnterEvent event) {
        Class<?> navigationTarget = event.getNavigationTarget();
        if (navigationTarget != null) {
            var tab = pagesToTab.get(navigationTarget);
            tabMenu.setSelectedTab(tab);
        } else {
            tabMenu.setSelectedTab(null);
        }
    }

    /**
     * Set tab enabled if the view is accessible by the current user.
     */
    private void setTabEnabledOnUserRole() {
        pagesToTab.entrySet()
                  .forEach(entry -> entry.getValue()
                                         .setEnabled(
                                             SecurityUtils.isUserAllowedToView(entry.getKey())
                                         )
                  );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSelectedTab(event);
        setTabEnabledOnUserRole();
    }

    /**
     * TabBuilder is a utility class that helps in building tabs for a tab menu. It provides methods
     * to set the icon, label, and component of the tab. Tabs can be added to the tab menu using the
     * `addForComponent` method.
     */
    public class TabBuilder {
        private Icon tabIcon;
        private String tabLabel = "";
        private Class<? extends Component> clz;

        private TabBuilder() {
        }

        public TabBuilder withIcon(Icon icon) {
            this.tabIcon = icon;
            return this;
        }

        public TabBuilder withIcon(VaadinIcon icon) {
            this.tabIcon = new Icon(icon);
            return this;
        }

        public TabBuilder withLabel(String label) {
            this.tabLabel = label;
            return this;
        }

        public Tab addForComponent(Component component) {
            clz = component.getClass();
            return addForComponent(clz);
        }

        /**
         * Adds a new tab to the tab menu for a specified component class.
         *
         * @param clz the class of the component to be associated with the tab
         * @return the created tab
         * @throws IllegalArgumentException if the component class is null
         */
        public Tab addForComponent(Class clz) {
            if (clz == null) {
                throw new IllegalArgumentException("component is not allowed to be null!");
            }

            var tabText = new Span(tabLabel);
            tabText.getStyle().set("font-size", tabFontSize);

            var tab = new Tab(tabText);
            if (tabIcon != null) {
                tabIcon.setSize(tabFontSize);
                var tabLayout = new HorizontalLayout(tabIcon, tabText);
                tabLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                tab = new Tab(tabLayout);
            }

            tabsToPages.put(tab, clz);
            pagesToTab.put(clz, tab);
            tabMenu.add(tab);
            return tab;
        }
    }

    /**
     * Creates tabs based on the specified group from the ApplicationContext.
     *
     * @param applicationContext the ApplicationContext containing the beans with TabMetadata
     *                           annotations
     * @param group              the group to filter the tabs
     */
    public void createTabs(ApplicationContext applicationContext, String group) {
        // this breaks lazy loading...
        applicationContext.getBeansWithAnnotation(TabMetadata.class)
                          .values().stream()
                          .map(o -> (Component) o)
                          .filter(c -> c.getClass().getAnnotation(TabMetadata.class).tabGroup()
                                        .equals(group)
                          )
                          .sorted(AnnotationAwareOrderComparator.INSTANCE)
                          .forEach(c -> {
                              TabMetadata annotation =
                                  c.getClass().getAnnotation(TabMetadata.class);

                              LOGGER.debug(
                                  "Adding tab [{}] with title [{}] to group [{}]", c,
                                  annotation.title(), group
                              );
                              this.createTab()
                                  .withLabel(annotation.title())
                                  .addForComponent(c.getClass());
                          });
    }
}
