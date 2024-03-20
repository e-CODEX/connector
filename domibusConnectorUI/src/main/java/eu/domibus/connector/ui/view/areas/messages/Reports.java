package eu.domibus.connector.ui.view.areas.messages;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.FooterRow.FooterCell;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.dto.WebReport;
import eu.domibus.connector.ui.dto.WebReportEntry;
import eu.domibus.connector.ui.service.WebReportsService;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
@UIScope
@Route(value = Reports.ROUTE, layout = MessageLayout.class)
@Order(4)
@TabMetadata(title = "Reports", tabGroup = MessageLayout.TAB_GROUP_NAME)
public class Reports extends VerticalLayout {

	public static final String ROUTE = "reports";

	private WebReportsService reportsService;
	
	Date fromDateValue;
	Date toDateValue;
	boolean includeEvidencesValue;
	
	VerticalLayout reportDataArea = new VerticalLayout(); 

	public Reports(@Autowired WebReportsService reportsService) {
		
		this.reportsService = reportsService;
		
		VerticalLayout reportFormArea = new VerticalLayout(); 
		
		Div details = new Div();
		details.setWidth("300px");
		
		DatePicker fromDate = new DatePicker();
		fromDate.setLocale(Locale.ENGLISH);
		fromDate.setLabel("From Date");
		fromDate.setErrorMessage("From Date invalid!");
		fromDate.addValueChangeListener(e1 -> fromDateValue = asDate(fromDate.getValue()));
		details.add(fromDate);
		
		DatePicker toDate = new DatePicker();
		toDate.setLocale(Locale.ENGLISH);
		toDate.setLabel("To Date");
		toDate.setErrorMessage("To Date invalid!");
		toDate.addValueChangeListener(e2 -> toDateValue = asDate(toDate.getValue()));
		toDate.setEnabled(true);
		details.add(toDate);
		
		LumoCheckbox includeEvidences = new LumoCheckbox();
		includeEvidences.setLabel("Include sent evidences as messages");
		includeEvidences.addValueChangeListener(e -> includeEvidencesValue = includeEvidences.getValue().booleanValue());
		details.add(includeEvidences);
		
		Button submit = new Button();
		submit.addClickListener(e -> generateReport());
		submit.setText("Generate Report");
		details.add(submit);
		
		reportFormArea.add(details);
		
		setSizeFull();
		reportFormArea.setWidth("300px");
		add(reportFormArea);

//		setHeight("100vh");
	}
	
	



	private void generateReport() {
		//Date toDateInclusive = new Date(toDateValue.getTime() + TimeUnit.DAYS.toMillis( 1 ));
		
		List<WebReportEntry> generatedReport = reportsService.generateReport(fromDateValue, toDateValue, includeEvidencesValue);
		
		if(!CollectionUtils.isEmpty(generatedReport)) {
			reportDataArea.removeAll();
			
			List<WebReport> sortReport = sortReport(generatedReport);
			
			for(WebReport report:sortReport) {
				Div details = new Div();
				details.setWidth("100vw");
				details.getStyle().set("margin", "unset");
				
				Grid<WebReportEntry> grid = new Grid<>();
				
				grid.setItems(report.getEntries());
				
				Column<WebReportEntry> partyCol = grid.addColumn(WebReportEntry::getParty).setHeader("Party").setWidth("200px");
				Column<WebReportEntry> serviceCol = grid.addColumn(WebReportEntry::getService).setHeader("Service").setWidth("300px");
				Column<WebReportEntry> receivedCol = grid.addColumn(WebReportEntry::getReceived).setHeader("Messages received from").setWidth("300px");
				Column<WebReportEntry> sentCol = grid.addColumn(WebReportEntry::getSent).setHeader("Messages sent to").setWidth("300px");
				
				HeaderRow topRow = grid.prependHeaderRow();
				
				

				HeaderCell informationCell = topRow.join(partyCol, serviceCol, receivedCol, sentCol);
				informationCell.setText(report.getPeriod());
				
				FooterRow footerRow = grid.appendFooterRow();
				FooterCell totalsCell = footerRow.getCell(serviceCol);
				totalsCell.setText("Total:");
				FooterCell receivedCell = footerRow.getCell(receivedCol);
				receivedCell.setText(Long.toString(report.getSumReceived()));
				FooterCell sentCell = footerRow.getCell(sentCol);
				sentCell.setText(Long.toString(report.getSumSent()));
				
				grid.setWidth("1150px");
				grid.setHeight("150px");
				grid.setMultiSort(false);
				
//				for(Column<WebReportEntry> col : grid.getColumns()) {
//					col.setSortable(true);
//					col.setResizable(true);
//				}
				details.add(grid);
				
				reportDataArea.add(details);
				
			}
			
			Div downloadExcel = new Div();
			
			Button download = new Button();
			download.setIcon(new Image("frontend/images/xls.png", "XLS"));
			
			download.addClickListener(e -> {
			
				Element file = new Element("object");
				Element dummy = new Element("object");
				
				Input oName = new Input();
				
				String name = "MessagesReport.xls";
				
				StreamResource resource = new StreamResource(name,() -> getMessagesReport(sortReport));
				
				resource.setContentType("application/xls");
				
				file.setAttribute("data", resource);
				
				Anchor link = null;
				link = new Anchor(file.getAttribute("data"), "Download Document");
				
				UI.getCurrent().getElement().appendChild(oName.getElement(), file,
						dummy);
				oName.setVisible(false);
				file.setVisible(false);
				this.getUI().get().getPage().executeJavaScript("window.open('"+link.getHref()+"');");
			});
			
			downloadExcel.add(download);
			
			reportDataArea.add(downloadExcel);
			
//			reportDataArea.setHeight("100vh");
			reportDataArea.setWidth("100vw");
			add(reportDataArea);
		}
	}
	
	private InputStream getMessagesReport(List<WebReport> report) {
		
		
		return reportsService.generateExcel(fromDateValue, toDateValue, report);
	}
	
	
	
	private List<WebReport> sortReport(List<WebReportEntry> result) {
		Map<String, WebReport> periodMap = new HashMap<String, WebReport>();

        for (WebReportEntry entry : result) {
            String period = entry.getMonth() + "/" + entry.getYear();
            if (!periodMap.containsKey(period)) {
            	WebReport p = new WebReport();
                p.setPeriod(period);
                p.setYear(entry.getYear());
                p.setMonth(entry.getMonth().length() > 1 ? entry.getMonth() : "0" + entry.getMonth());
                p.setEntries(new ArrayList<WebReportEntry>());
                periodMap.put(period, p);
            }

            periodMap.get(period).getEntries().add(entry);
            periodMap.get(period).setSumReceived(periodMap.get(period).getSumReceived() + entry.getReceived());
            periodMap.get(period).setSumSent(periodMap.get(period).getSumSent() + entry.getSent());
        }

        List<WebReport> periods = new ArrayList<WebReport>(periodMap.size());
        periods.addAll(periodMap.values());

        Collections.sort(periods, new Comparator<WebReport>() {
            @Override
            public int compare(WebReport c1, WebReport c2) {

                String p1 = c1.getYear() + c1.getMonth();
                String p2 = c2.getYear() + c2.getMonth();

                return p1.compareTo(p2);
            }
        });
        
        return periods;
	}
	
	public static Date asDate(LocalDate localDate) {
	    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	  }

}
