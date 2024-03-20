package eu.domibus.connector.ui.dto;

import java.util.Date;

public class WebMessageEvidence {

	private String evidenceType;
	private Date deliveredToGateway;
	private Date deliveredToBackend;
	
	public String getEvidenceType() {
		return evidenceType;
	}

	public void setEvidenceType(String evidenceType) {
		this.evidenceType = evidenceType;
	}

	public Date getDeliveredToGateway() {
		return deliveredToGateway;
	}
	
	public String getDeliveredToGatewayString() {
		return deliveredToGateway!=null?deliveredToGateway.toString():null;
	}

	public void setDeliveredToGateway(Date deliveredToGateway) {
		this.deliveredToGateway = deliveredToGateway;
	}
	
	public void setDeliveredToGatewayString(String deliveredToGateway) {
		}

	public Date getDeliveredToBackend() {
		return deliveredToBackend;
	}
	
	public String getDeliveredToBackendString() {
		return deliveredToBackend!=null?deliveredToBackend.toString():null;
	}

	public void setDeliveredToBackend(Date deliveredToBackend) {
		this.deliveredToBackend = deliveredToBackend;
	}
	
	public void setDeliveredToBackendString(String deliveredToBackend) {
	}

}
