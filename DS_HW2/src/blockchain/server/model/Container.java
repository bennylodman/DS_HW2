package blockchain.server.model;

import java.util.HashSet;
import java.util.Set;

import blockchain.server.group.TransactionResult;
import utils.GeneralUtilities;

public class Container extends SupplyChainObject {
	public static String PREFIX = "C";
	
	private String ship;
	private Set<String> items;
	private String doc;
	
	public Container(String containerId) {
		this(containerId, null);
	}
	
	public Container(String containerId, String shipId) {
		super.id = containerId;
		super.deleted = false;
		this.ship = shipId;
		this.items = new HashSet<>();
	}
	
	public String getShip() {
		return ship;
	}
	
	public void setShip(String ship) {
		this.ship = ship;
	}
	
	public void addItem(String itemsId) {
		items.add(itemsId);
	}
	
	public void removeItem(String itemsId) {
		items.remove(itemsId);
	}
	
	public boolean hasItem(String itemsId) {
		return items.contains(itemsId);
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}
	
	public boolean isOnDoc() {
		return this.ship.isEmpty();
	}
	
	public boolean isEmpty() {
		return this.items.isEmpty();
	}
	
	public Container deepCopy() {
		return GeneralUtilities.deepCopy(this, Container.class);
	}
	
	public void delete(SupplyChainView view) {
		synchronized (view) {
			Ship ship = ((Ship) view.getObjectState(this.ship)).deepCopy();
			Container containerNextState = this.deepCopy(); 
			
			containerNextState.setShip("None");
			containerNextState.setDoc("None");
			containerNextState.setDeleted(true);
			ship.removeContainer(containerNextState.getId());
			
			view.addNextState(containerNextState);
			view.addNextState(ship);
		}
	}
	
	public TransactionResult verifyDelete(SupplyChainView view) {
		synchronized (view) {
			if (this.id == null || !this.id.startsWith(Container.PREFIX))
				return new TransactionResult(false, "ERROR: " + id + " is invalid container ID");
			
			if (!view.hasObject(this.id))
				return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + this.id);
			
			if (this.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + this.id + " has already been deleted");
			
			Container container = ((Container) view.getObjectState(this.id));
			if (!container.isEmpty())
				return new TransactionResult(false, "ERROR: The container " + this.id + " is not empty");
			
			return new TransactionResult(true, "OK");
		}
	}
	
	public void updateItemsState(SupplyChainView view, String newShipId, String newDocId) {
		synchronized (view) {
			for (String itemId : this.items) {
				Item itemNextState = ((Item) view.getObjectState(itemId)).deepCopy();
				
				if (newShipId != null)
					itemNextState.setShip(newShipId);
				
				if (newDocId != null)
					itemNextState.setDoc(newDocId);
				
				view.addNextState(itemNextState);
			}
		}
	}
	
	public TransactionResult verifyMove(String src, String trg, SupplyChainView currentView) {
		if (src == null || !src.startsWith(Ship.PREFIX))
			return new TransactionResult(false, "ERROR: " + src + " is invalid ship ID");
		
		if (trg == null || !trg.startsWith(Ship.PREFIX))
			return new TransactionResult(false, "ERROR: " + trg + " is invalid ship ID");
		
		if (!currentView.hasObject(src))
			return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + src);
		
		if (!currentView.hasObject(trg))
			return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + trg);
		
		Ship srcShip = (Ship) currentView.getObjectState(src);
		Ship trgShip = (Ship) currentView.getObjectState(trg);
		
		if (this.isDeleted())
			return new TransactionResult(false, "ERROR: The object " + this.id + " has been deleted"); 
		
		if (srcShip.isDeleted())
			return new TransactionResult(false, "ERROR: The object " + srcShip.getId() + " has been deleted");
		
		if (trgShip.isDeleted())
			return new TransactionResult(false, "ERROR: The object " + trgShip.getId() + " has been deleted");
		
		if (this.ship != src)
			return new TransactionResult(false, "ERROR: The container " + this.getId() + " is not on the ship " + src);
		
		if (srcShip.getDoc() != trgShip.getDoc())
			return new TransactionResult(false,  "ERROR: " + src + " and " + trg + " are not on the same dock");
		
		return new TransactionResult(true, "OK");
	}
	
	public void move(String src, String trg, SupplyChainView currentView) {
		synchronized (currentView) {
			Ship srcShip = ((Ship) currentView.getObjectState(src)).deepCopy();
			Ship trgShip = ((Ship) currentView.getObjectState(trg)).deepCopy();
			Container containerNextState = this.deepCopy();
			
			containerNextState.setShip(trgShip.getId());
			containerNextState.updateItemsState(currentView, trgShip.getId(), null);
			srcShip.removeContainer(getId());
			trgShip.addContainer(getId());
			
			currentView.addNextState(containerNextState);
			currentView.addNextState(srcShip);
			currentView.addNextState(trgShip);
		}
	}
}

