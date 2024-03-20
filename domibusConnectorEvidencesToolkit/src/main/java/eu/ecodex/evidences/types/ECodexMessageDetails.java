package eu.ecodex.evidences.types;

public class ECodexMessageDetails {
	private String senderAddress;
	private String recipientAddress;
	private String nationalMessageId;
	private String ebmsMessageId;
	private byte[] hashValue;
	private String hashAlgorithm;
	
	
	public String getSenderAddress() {
		return senderAddress;
	}
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	public String getRecipientAddress() {
		return recipientAddress;
	}
	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}
	public String getNationalMessageId() {
		return nationalMessageId;
	}
	public void setNationalMessageId(String nationalMessageId) {
		this.nationalMessageId = nationalMessageId;
	}
	public String getEbmsMessageId() {
		return ebmsMessageId;
	}
	public void setEbmsMessageId(String ebmsMessageId) {
		this.ebmsMessageId = ebmsMessageId;
	}
	public byte[] getHashValue() {
		return hashValue;
	}
	public void setHashValue(byte[] hashValue) {
		this.hashValue = hashValue;
	}
	public String getHashAlgorithm() {
		return hashAlgorithm;
	}
	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}
	
	
}
