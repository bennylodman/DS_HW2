package blockchain.server.model;

import java.util.ArrayList;
import java.util.List;

public class QueryResult {
	private boolean status;
	private String message;
	private List<SupplyChainObject> requestedObjects;
	
	public QueryResult(boolean status, String message) {
		this.status = status;
		this.message = message;
		this.requestedObjects = null;
	}
	
	public QueryResult(boolean status, String message, SupplyChainObject obj) {
		this.status = status;
		this.message = message;
		this.requestedObjects = new ArrayList<>();
		this.requestedObjects.add(obj);
	}
	
	public QueryResult(boolean status, String message, List<SupplyChainObject> requestedObjects) {
		this.status = status;
		this.message = message;
		this.requestedObjects = requestedObjects;
	}
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<SupplyChainObject> getRequestedObjects() {
		return requestedObjects;
	}
	public void setRequestedObjects(List<SupplyChainObject> requestedObjects) {
		this.requestedObjects = requestedObjects;
	}
	
	
}
