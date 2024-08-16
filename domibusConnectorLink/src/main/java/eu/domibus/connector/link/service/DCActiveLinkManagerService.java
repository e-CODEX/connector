/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.link.service;

import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.api.ActiveLink;
import eu.domibus.connector.link.api.ActiveLinkPartner;
import eu.domibus.connector.link.api.LinkPlugin;
import eu.domibus.connector.link.api.exception.LinkPluginException;
import eu.domibus.connector.tools.LoggingMDCPropertyNames;
import eu.domibus.connector.tools.logging.LoggingMarker;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * This class manages the lifecycle of the connector links.
 */
@SuppressWarnings("checkstyle:LocalVariableName")
@Service
@ConditionalOnBean(DCLinkPluginConfiguration.class)
public class DCActiveLinkManagerService {
    private static final Logger LOGGER = LogManager.getLogger(DCActiveLinkManagerService.class);
    private final Scheduler scheduler;
    private final List<LinkPlugin> linkPluginFactories;
    private final Map<DomibusConnectorLinkPartner.LinkPartnerName, ActiveLinkPartner>
        activeLinkPartners = new ConcurrentHashMap<>();
    private final Map<DomibusConnectorLinkConfiguration.LinkConfigName, ActiveLink>
        activeLinkConfigurations = new ConcurrentHashMap<>();

    /**
     * The DCActiveLinkManagerService class is responsible for managing the active links and link
     * partners in the Domibus Connector.
     *
     * @param scheduler           the scheduler used for scheduling link configuration jobs
     * @param linkPluginFactories the list of LinkPlugins used for configuring and managing links
     */
    public DCActiveLinkManagerService(
        Scheduler scheduler,
        @Autowired(required = false) List<LinkPlugin> linkPluginFactories) {
        this.scheduler = scheduler;
        if (linkPluginFactories == null) {
            linkPluginFactories = new ArrayList<>();
        }
        this.linkPluginFactories = linkPluginFactories;
    }

    Optional<SubmitToLinkPartner> getSubmitToLinkPartner(String linkName) {
        if (!StringUtils.hasLength(linkName)) {
            throw new IllegalArgumentException("Provided link name is empty!");
        }
        return getSubmitToLinkPartner(new DomibusConnectorLinkPartner.LinkPartnerName(linkName));
    }

    Optional<SubmitToLinkPartner> getSubmitToLinkPartner(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        var activeLinkPartner = activeLinkPartners.get(linkPartnerName);
        if (activeLinkPartner == null) {
            var error = String.format("No linkPartner with name %s available", linkPartnerName);
            throw new LinkPluginException(error);
        }
        SubmitToLinkPartner submitToLinkBean =
            activeLinkPartner.getParentLink().getLinkPlugin().getSubmitToLink(activeLinkPartner);
        return Optional.of(submitToLinkBean);
    }

    /**
     * Retrieves the PullFromLinkPartner for the given link name.
     *
     * @param linkName the name of the link partner
     * @return an Optional containing the PullFromLinkPartner, or empty if not found
     * @throws LinkPluginException if no link partner with the given name is available
     */
    public Optional<PullFromLinkPartner> getPullFromLinkPartner(String linkName) {
        var linkPartnerName = new DomibusConnectorLinkPartner.LinkPartnerName(linkName);
        var activeLinkPartner = activeLinkPartners.get(linkPartnerName);
        if (activeLinkPartner == null) {
            var error = String.format("No linkPartner with name %s available", linkName);
            throw new LinkPluginException(error);
        }
        return activeLinkPartner.getParentLink().getLinkPlugin().getPullFromLink(activeLinkPartner);
    }

    public List<LinkPlugin> getAvailableLinkPlugins() {
        return this.linkPluginFactories;
    }

    /**
     * Retrieves a LinkPlugin by its name.
     *
     * @param name the name of the LinkPlugin to retrieve
     * @return an Optional containing the LinkPlugin, or an empty Optional if not found
     */
    public Optional<LinkPlugin> getLinkPluginByName(String name) {
        return this.linkPluginFactories
            .stream()
            .filter(lp -> lp.getPluginName().equals(name))
            .findFirst();
    }

    /**
     * Activates a link partner in the Domibus Connector.
     *
     * @param linkInfo the DomibusConnectorLinkPartner representing the link partner
     * @return an Optional containing the ActiveLinkPartner if activation is successful, otherwise
     *      empty
     * @throws IllegalArgumentException if the link partner name or link configuration is null
     * @throws LinkPluginException      if the link partner could not be activated
     */
    public synchronized Optional<ActiveLinkPartner> activateLinkPartner(
        DomibusConnectorLinkPartner linkInfo) {
        try (var li = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_LINK_PARTNER_NAME,
            linkInfo.getLinkPartnerName().toString()
        )) {
            DomibusConnectorLinkConfiguration linkConfiguration = linkInfo.getLinkConfiguration();
            DomibusConnectorLinkConfiguration.LinkConfigName configName =
                linkConfiguration.getConfigName();
            final var linkPartnerName = linkInfo.getLinkPartnerName();

            if (linkPartnerName == null) {
                throw new IllegalArgumentException(
                    "LinkPartnerName of LinkPartner is not allowed to be null!");
            }
            if (configName == null) {
                throw new IllegalArgumentException(
                    "ConfigName of LinkConfiguration is not allowed to be null!");
            }

            var activeLink = activeLinkConfigurations.get(configName);
            if (activeLink == null) {
                activeLink = startLinkConfiguration(linkInfo.getLinkConfiguration());
            }
            if (activeLink == null) {
                LOGGER.warn("No link configuration for link partner available [{}]!", linkInfo);
                return Optional.empty();
            }
            activeLinkConfigurations.put(configName, activeLink);

            var linkPlugin = activeLink.getLinkPlugin();

            ActiveLinkPartner activeLinkPartner = null;
            try {
                activeLinkPartner = linkPlugin.enableLinkPartner(linkInfo, activeLink);
                activeLinkPartners.put(linkPartnerName, activeLinkPartner);
                configurePull(linkInfo, activeLinkPartner);
            } catch (LinkPluginException e) {
                LOGGER.warn("Link partner could not be activated", e);
            }

            return Optional.ofNullable(activeLinkPartner);
        } catch (Exception e) {
            var error = String.format(
                "Error while activating Link Partner [%s]",
                linkInfo.getLinkPartnerName()
            );
            throw new LinkPluginException(error, e);
        }
    }

    private void unconfigurePull(
        DomibusConnectorLinkPartner linkInfo, ActiveLinkPartner activeLinkPartner) {
        if (linkInfo.getRcvLinkMode() != LinkMode.PULL) {
            return;
        }
        Optional<PullFromLinkPartner> pullFromBean = getPullFromLinkPartner(
            activeLinkPartner.getLinkPartner().getLinkPartnerName().getLinkName());
        if (!pullFromBean.isPresent()) {
            LOGGER.warn("PULL MODE activated but NO pull bean found!");
            return;
        }
        try {

            var linkPartnerName = linkInfo.getLinkPartnerName().toString();

            // Delete Job and recreate it...
            var jobKey = createJobKey(linkPartnerName);
            scheduler.deleteJob(jobKey);

            // same for the trigger...
            var triggerKey = createTriggerKey(linkPartnerName);
            scheduler.unscheduleJob(triggerKey);
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG, "Deactivating pull job for LinkPartner [{}]",
                linkPartnerName
            );
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configures scheduler for pull mode for the given link partner.
     *
     * @param linkInfo          the DomibusConnectorLinkPartner representing the link partner
     * @param activeLinkPartner the ActiveLinkPartner representing the active link partner
     */
    private void configurePull(
        DomibusConnectorLinkPartner linkInfo, ActiveLinkPartner activeLinkPartner) {
        if (linkInfo.getRcvLinkMode() != LinkMode.PULL) {
            return;
        }
        Optional<PullFromLinkPartner> pullFromBean = getPullFromLinkPartner(
            activeLinkPartner.getLinkPartner().getLinkPartnerName().getLinkName());
        if (!pullFromBean.isPresent()) {
            LOGGER.warn("PULL MODE activated but NO pull bean found!");
            return;
        }
        try {
            var linkPartnerName = linkInfo.getLinkPartnerName().toString();
            // first deconfigure trigger and/or job...
            unconfigurePull(linkInfo, activeLinkPartner);

            // create job
            var jobKey = createJobKey(linkPartnerName);
            var link_pulls = JobBuilder.newJob(DCLinkPullJob.class)
                                       .storeDurably(true)
                                       .withIdentity(jobKey)
                                       .build();

            var pullIntervalSeconds = (int) linkInfo.getPullInterval().get(ChronoUnit.SECONDS);

            // create trigger
            var triggerKey = createTriggerKey(linkPartnerName);
            var link_pull_trigger = TriggerBuilder
                .newTrigger()
                .forJob(link_pulls)
                .withIdentity(triggerKey)
                .usingJobData(
                    DCLinkPullJob.LINK_PARTNER_NAME_PROPERTY_NAME,
                    linkInfo.getLinkPartnerName().toString()
                )
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(pullIntervalSeconds))
                .build();

            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                "Setting up trigger with pull interval [{} seconds] to pull from [{}]",
                pullIntervalSeconds, linkPartnerName
            );

            scheduler.scheduleJob(link_pulls, link_pull_trigger);
        } catch (SchedulerException e) {
            var error =
                String.format("An error occurred while configuring pull for link [%s]", linkInfo);
            throw new LinkPluginException(error, e);
        }
    }

    @NotNull
    private TriggerKey createTriggerKey(String linkPartnerName) {
        return new TriggerKey("pull_trigger_" + linkPartnerName, "LINK_PULL_TRIGGER");
    }

    @NotNull
    private JobKey createJobKey(String linkPartnerName) {
        return new JobKey("pull_from_" + linkPartnerName, "LINK_PULL_JOBS");
    }

    private synchronized ActiveLink startLinkConfiguration(
        DomibusConnectorLinkConfiguration linkConfiguration) {
        try (var lc = MDC.putCloseable(
            LoggingMDCPropertyNames.MDC_LINK_CONFIG_NAME,
            linkConfiguration.getConfigName().toString()
        )) {
            String linkImpl = linkConfiguration.getLinkImpl();
            if (!StringUtils.hasLength(linkImpl)) {
                var error = String.format(
                    "link impl of [%s] is empty! No link configuration can be created!",
                    linkConfiguration
                );
                throw new LinkPluginException(error);
            }
            Optional<LinkPlugin> first =
                linkPluginFactories.stream().filter(l -> l.canHandle(linkImpl)).findFirst();
            if (!first.isPresent()) {
                var error = String.format(
                    "No link factory for linkImpl [%s] found! No link configuration will be "
                        + "created!", linkImpl
                );
                throw new LinkPluginException(error);
            }
            var linkPlugin = first.get();

            ActiveLink link = linkPlugin.startConfiguration(linkConfiguration);
            if (link == null) {
                throw new LinkPluginException(
                    String.format("Failed to start configuration [%s]", linkConfiguration));
            }
            link.setLinkPlugin(linkPlugin);

            return link;
        }
    }

    /**
     * Shuts down the active link partner with the given link partner name.
     *
     * @param linkPartnerName the name of the link partner to be shut down
     * @throws LinkPluginException if no active link partner with the given name is found
     */
    public void shutdownLinkPartner(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        var activeLinkPartner = activeLinkPartners.get(linkPartnerName);
        if (activeLinkPartner == null) {
            throw new LinkPluginException(String.format(
                "No active linkPartner with name %s found!",
                linkPartnerName.toString()
            ));
        }

        this.unconfigurePull(activeLinkPartner.getLinkPartner(), activeLinkPartner);
        activeLinkPartners.remove(linkPartnerName);

        activeLinkPartner.getParentLink().getLinkPlugin()
                         .shutdownActiveLinkPartner(activeLinkPartner);
    }

    /**
     * This method is invoked when the preDestroy event occurs, typically during the shutdown or
     * destruction of the object that contains this method. It performs the necessary cleanup tasks
     * before the object is destroyed.
     */
    @PreDestroy
    public void preDestroy() {
        this.activeLinkConfigurations.forEach((key, value) -> {
            try {
                LOGGER.info("Invoking shutdown on LinkConfig [{}]", value);
                value.getLinkPlugin().shutdownConfiguration(value);
            } catch (Exception e) {
                LOGGER.error("Exception occured during shutdown LinkConfig", e);
            }
        });
    }

    public Collection<ActiveLinkPartner> getActiveLinkPartners() {
        return activeLinkPartners.values();
    }

    public Optional<ActiveLinkPartner> getActiveLinkPartnerByName(String lp) {
        var linkPartnerName = new DomibusConnectorLinkPartner.LinkPartnerName(lp);
        return getActiveLinkPartnerByName(linkPartnerName);
    }

    public Optional<ActiveLinkPartner> getActiveLinkPartnerByName(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        var activeLinkPartner = activeLinkPartners.get(linkPartnerName);
        return Optional.ofNullable(activeLinkPartner);
    }
}
