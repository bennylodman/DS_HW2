package blockchain.server.group;

import java.util.List;

public class ResponseHolder {
	private String id;
	private Object lock;
	private boolean doneCommunication;
	private List<SupplyChainMessage> responsesList;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isDoneCommunication() {
		return doneCommunication;
	}
	
	public void setDoneCommunication(boolean doneCommunication) {
		this.doneCommunication = doneCommunication;
	}
	
}
