/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.messages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.component.LumoCheckbox;
import eu.ecodex.connector.ui.dto.WebReport;
import eu.ecodex.connector.ui.dto.WebReportEntry;
import eu.ecodex.connector.ui.service.WebReportsService;
import eu.ecodex.connector.ui.utils.UiStyle;
import eu.ecodex.connector.ui.view.areas.configuration.TabMetadata;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * The {@code Reports} class represents a component for generating and displaying reports.
 */
@Component
@UIScope
@Route(value = Reports.ROUTE, layout = MessageLayout.class)
@Order(4)
@TabMetadata(title = "Reports", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class Reports extends VerticalLayout {
    public static final String ROUTE = "reports";
    private final WebReportsService reportsService;
    Date fromDateValue;
    Date toDateValue;
    boolean includeEvidencesValue;
    VerticalLayout reportDataArea = new VerticalLayout();

    /**
     * The Reports class is responsible for creating a report form with various input fields and a
     * submit button.
     *
     * @param reportsService The WebReportsService instance used to generate the report. Cannot be
     *                       null.
     */
    public Reports(@Autowired WebReportsService reportsService) {
        this.reportsService = reportsService;

        var details = new Div();
        details.setWidth(UiStyle.WIDTH_300_PX);

        var fromDate = new DatePicker();
        fromDate.setLocale(Locale.ENGLISH);
        fromDate.setLabel("From Date");
        fromDate.setErrorMessage("From Date invalid!");
        fromDate.addValueChangeListener(e1 -> fromDateValue = asDate(fromDate.getValue()));
        details.add(fromDate);

        var toDate = new DatePicker();
        toDate.setLocale(Locale.ENGLISH);
        toDate.setLabel("To Date");
        toDate.setErrorMessage("To Date invalid!");
        toDate.addValueChangeListener(e2 -> toDateValue = asDate(toDate.getValue()));
        toDate.setEnabled(true);
        details.add(toDate);

        var includeEvidences = new LumoCheckbox();
        includeEvidences.setLabel("Include sent evidences as messages");
        includeEvidences.addValueChangeListener(
            e -> includeEvidencesValue = includeEvidences.getValue());
        details.add(includeEvidences);

        var submit = new Button();
        submit.addClickListener(e -> generateReport());
        submit.setText("Generate Report");
        details.add(submit);

        var reportFormArea = new VerticalLayout();
        reportFormArea.add(details);

        setSizeFull();
        reportFormArea.setWidth(UiStyle.WIDTH_300_PX);
        add(reportFormArea);
    }

    private void generateReport() {
        var generatedReport =
            reportsService.generateReport(fromDateValue, toDateValue, includeEvidencesValue);

        if (!CollectionUtils.isEmpty(generatedReport)) {
            reportDataArea.removeAll();
            var sortReport = sortReport(generatedReport);
            for (var report : sortReport) {
                var details = new Div();
                details.setWidth("100vw");
                details.getStyle().set("margin", "unset");

                Grid<WebReportEntry> grid = new Grid<>();

                grid.setItems(report.getEntries());

                var partyCol =
                    grid.addColumn(WebReportEntry::getParty).setHeader("Party").setWidth("200px");
                var serviceCol =
                    grid.addColumn(WebReportEntry::getService).setHeader("Service")
                        .setWidth(UiStyle.WIDTH_300_PX);
                var receivedCol =
                    grid.addColumn(WebReportEntry::getReceived).setHeader("Messages received from")
                        .setWidth(UiStyle.WIDTH_300_PX);
                Column<WebReportEntry> sentCol =
                    grid.addColumn(WebReportEntry::getSent).setHeader("Messages sent to")
                        .setWidth(UiStyle.WIDTH_300_PX);

                var topRow = grid.prependHeaderRow();

                var informationCell = topRow.join(partyCol, serviceCol, receivedCol, sentCol);
                informationCell.setText(report.getPeriod());

                var footerRow = grid.appendFooterRow();
                var totalsCell = footerRow.getCell(serviceCol);
                totalsCell.setText("Total:");
                var receivedCell = footerRow.getCell(receivedCol);
                receivedCell.setText(Long.toString(report.getSumReceived()));
                var sentCell = footerRow.getCell(sentCol);
                sentCell.setText(Long.toString(report.getSumSent()));

                grid.setWidth("1150px");
                grid.setHeight("150px");
                grid.setMultiSort(false);

                details.add(grid);

                reportDataArea.add(details);
            }

            var downloadExcel = new Div();

            var download = new Button();
            download.setIcon(new Image("frontend/images/xls.png", "XLS"));

            download.addClickListener(e -> {
                var file = new Element("object");
                var name = "MessagesReport.xls";

                var resource = new StreamResource(name, () -> getMessagesReport(sortReport));
                resource.setContentType("application/xls");
                file.setAttribute("data", resource);
                Anchor link;
                link = new Anchor(file.getAttribute("data"), "Download Document");

                var objectName = new Input();
                var dummy = new Element("object");
                UI.getCurrent().getElement().appendChild(objectName.getElement(), file, dummy);
                objectName.setVisible(false);
                file.setVisible(false);
                this.getUI()
                    .get()
                    .getPage()
                    .open(link.getHref());
            });

            downloadExcel.add(download);

            reportDataArea.add(downloadExcel);

            reportDataArea.setWidth("100vw");
            add(reportDataArea);
        }
    }

    private InputStream getMessagesReport(List<WebReport> report) {

        return reportsService.generateExcel(fromDateValue, toDateValue, report);
    }

    private List<WebReport> sortReport(List<WebReportEntry> result) {
        Map<String, WebReport> periodMap = new HashMap<>();

        for (WebReportEntry entry : result) {
            String period = entry.getMonth() + "/" + entry.getYear();
            if (!periodMap.containsKey(period)) {
                var webReport = new WebReport();
                webReport.setPeriod(period);
                webReport.setYear(entry.getYear());
                webReport.setMonth(
                    entry.getMonth().length() > 1 ? entry.getMonth() : "0" + entry.getMonth());
                webReport.setEntries(new ArrayList<>());
                periodMap.put(period, webReport);
            }

            periodMap.get(period).getEntries().add(entry);
            periodMap.get(period)
                     .setSumReceived(periodMap.get(period).getSumReceived() + entry.getReceived());
            periodMap.get(period).setSumSent(periodMap.get(period).getSumSent() + entry.getSent());
        }

        List<WebReport> periods = new ArrayList<>(periodMap.size());
        periods.addAll(periodMap.values());

        periods.sort((c1, c2) -> {
            String p1 = c1.getYear() + c1.getMonth();
            String p2 = c2.getYear() + c2.getMonth();

            return p1.compareTo(p2);
        });

        return periods;
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
