package blockchain.server.model;

import java.util.HashSet;
import java.util.Set;

import blockchain.server.group.TransactionResult;
import utils.GeneralUtilities;

public class Ship extends SupplyChainObject {
	public static String PREFIX = "S";
	
	private Set<String> containers;
	private String doc;
	
	public Ship(String shipId) {
		super.id = shipId;
		super.deleted = false;
		this.containers = new HashSet<String>();
	}
	
	public void addContainer(String containerId) {
		containers.add(containerId);
	}
	
	public void removeContainer(String containerId) {
		containers.remove(containerId);
	}
	
	public boolean hasContainer(String containerId) {
		return containers.contains(containerId);
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}
	
	public boolean isEmpty() {
		return this.containers.isEmpty();
	}
	
	public Ship deepCopy() {
		return GeneralUtilities.deepCopy(this, Ship.class);
	}
	
	public void delete(SupplyChainView view) {
		synchronized (view) {
			Ship shipNextState = this.deepCopy(); 
			shipNextState.setDoc("None");
			shipNextState.setDeleted(true);
			view.addNextState(shipNextState);
		}
	}
	
	public TransactionResult verifyDelete(SupplyChainView view) {
		synchronized (view) {
			if (this.id == null || !this.id.startsWith(Ship.PREFIX))
				return new TransactionResult(false, "ERROR: " + id + " is invalid ship ID");
			
			if (!view.hasObject(this.id))
				return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + this.id);
			
			if (this.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + this.id + " has already been deleted");
			
			Ship ship = ((Ship) view.getObjectState(this.id));
			if (!ship.isEmpty())
				return new TransactionResult(false, "ERROR: The ship " + this.id + " is not empty");
			
			return new TransactionResult(true, "OK");
		}
	}
	
	public TransactionResult verifyMove(String src, String trg, SupplyChainView currentView) {
		synchronized (currentView) {
			if (this.doc != src)
				return new TransactionResult(false, "ERROR: The ship " + this.getId() + " is not anchored in the dock " + src);
			
			if (this.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + this.id + " has been deleted");
			
			return new TransactionResult(true, "OK");
		}
	}
	
	public void move(String src, String trg, SupplyChainView currentView) {
		synchronized (currentView) {
			Ship shipNextState = this.deepCopy();
			shipNextState.setDoc(trg);
			
			for (String containerId : this.containers) {
				Container container = (Container) currentView.getObjectState(containerId);
				container.setDoc(trg);
				container.updateItemsState(currentView, null, trg);
				currentView.addNextState(container);
			}
			
			currentView.addNextState(shipNextState);
		}
	}
}
