package eu.domibus.connector.ui.utils;

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
import eu.domibus.connector.ui.configuration.SecurityUtils;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.HashMap;
import java.util.Map;


/**
 * Helper class for creating a tab menu
 * with support to navigate between this tabs
 * and also only show tabs which the corresponding view
 * can be accessed as enabled
 */
public class DCTabHandler implements BeforeEnterObserver {
    private static final Logger LOGGER = LogManager.getLogger(DCTabHandler.class);

    Tabs tabMenu = new Tabs();
    Map<Tab, Class> tabsToPages = new HashMap<>();
    Map<Class, Tab> pagesToTab = new HashMap<>();

    private String tabFontSize = "normal";

    public DCTabHandler() {
        tabMenu.addSelectedChangeListener(this::tabSelectionChanged);
    }

    public Tabs getTabs() {
        return this.tabMenu;
    }

    public String getTabFontSize() {
        return tabFontSize;
    }

    public void setTabFontSize(String tabFontSize) {
        this.tabFontSize = tabFontSize;
    }

    public TabBuilder createTab() {
        return new TabBuilder();
    }

    private void tabSelectionChanged(Tabs.SelectedChangeEvent selectedChangeEvent) {
        if (selectedChangeEvent.isFromClient()) {
            Tab selectedTab = selectedChangeEvent.getSelectedTab();
            Class componentClazz = tabsToPages.get(selectedTab);
            LOGGER.debug("Navigate to [{}]", componentClazz);
            UI.getCurrent().navigate(componentClazz);
        }
    }

    /**
     * sets the current selected tab dependent
     * on the current view
     */
    private void setSelectedTab(BeforeEnterEvent event) {
        Class<?> navigationTarget = event.getNavigationTarget();
        if (navigationTarget != null) {
            Tab tab = pagesToTab.get(navigationTarget);
            tabMenu.setSelectedTab(tab);
        } else {
            tabMenu.setSelectedTab(null);
        }
    }

    /**
     * set tab enabled if the view is accessable
     * by the current user
     */
    private void setTabEnabledOnUserRole() {
        pagesToTab.entrySet().stream()
                  .forEach(entry -> {
                      entry.getValue().setEnabled(SecurityUtils.isUserAllowedToView(entry.getKey()));
                  });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        setSelectedTab(event);
        setTabEnabledOnUserRole();
    }
    public void createTabs(ApplicationContext applicationContext, String group) {
        // this breaks lazy loading...
        applicationContext.getBeansWithAnnotation(TabMetadata.class)
                          .values().stream()
                          .map(o -> (Component) o)
                          .filter(c -> c.getClass().getAnnotation(TabMetadata.class).tabGroup().equals(group))
                          .sorted(AnnotationAwareOrderComparator.INSTANCE)
                          .forEach(c -> {
                              TabMetadata annotation = c.getClass().getAnnotation(TabMetadata.class);
                              LOGGER.debug(
                                      "Adding tab [{}] with title [{}] to group [{}]",
                                      c,
                                      annotation.title(),
                                      group
                              );
                              this.createTab()
                                  .withLabel(annotation.title())
                                  .addForComponent(c.getClass());
                          });
    }

    public class TabBuilder {
        private Icon tabIcon;
        private String tabLabel = "";
        private Component component;
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

        public Tab addForComponent(Class clz) {
            if (clz == null) {
                throw new IllegalArgumentException("component is not allowed to be null!");
            }

            Span tabText = new Span(tabLabel);
            tabText.getStyle().set("font-size", tabFontSize);

            Tab tab = new Tab(tabText);
            if (tabIcon != null) {
                tabIcon.setSize(tabFontSize);
                HorizontalLayout tabLayout = new HorizontalLayout(tabIcon, tabText);
                tabLayout.setAlignItems(FlexComponent.Alignment.CENTER);
                tab = new Tab(tabLayout);
            }

            tabsToPages.put(tab, clz);
            pagesToTab.put(clz, tab);
            tabMenu.add(tab);
            return tab;
        }
    }
}
