package blockchain.server.model;

import blockchain.server.group.TransactionResult;
import utils.GeneralUtilities;

public class Item extends SupplyChainObject {
	public static String PREFIX = "I";
	
	private String container;
	private String ship;
	private String doc;
	
	public Item(String itemId) {
		this(itemId, null, null);
	}
	
	public Item(String itemId, String containerId, String shipId) {
		super.id = itemId;
		super.deleted = false;
		this.container = containerId;
		this.ship = shipId;
	}
	
	public String getContainer() {
		return container;
	}
	
	public void setContainer(String container) {
		this.container = container;
	}
	
	public String getShip() {
		return ship;
	}
	
	public void setShip(String ship) {
		this.ship = ship;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}
	
	public Item deepCopy() {
		return GeneralUtilities.deepCopy(this, Item.class);
	}
	
	public void delete(SupplyChainView view) {
		synchronized (view) {
			Container container = ((Container) view.getObjectState(this.container)).deepCopy();
			Item itemNextState = this.deepCopy(); 
			
			itemNextState.setContainer("None");
			itemNextState.setShip("None");
			itemNextState.setDoc("None");
			itemNextState.setDeleted(true);
			container.removeItem(itemNextState.getId());
			
			view.addNextState(itemNextState);
			view.addNextState(container);
		}
	}
	
	public TransactionResult verifyDelete(SupplyChainView view) {
		synchronized (view) {
			if (this.id == null || !this.id.startsWith(Item.PREFIX))
				return new TransactionResult(false, "ERROR: " + id + " is invalid item ID");
			
			if (!view.hasObject(this.id))
				return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + this.id);
			
			if (this.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + this.id + " has already been deleted");
			
			return new TransactionResult(true, "OK");
		}
	}
	
	public TransactionResult verifyMove(String src, String trg, SupplyChainView view) {
		synchronized (view) {
			if (src == null || !src.startsWith(Container.PREFIX))
				return new TransactionResult(false, "ERROR: " + src + " is invalid container ID");
			
			if (trg == null || !trg.startsWith(Container.PREFIX))
				return new TransactionResult(false, "ERROR: " + trg + " is invalid container ID");
			
			if (!view.hasObject(src))
				return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + src);
			
			if (!view.hasObject(trg))
				return new TransactionResult(false, "ERROR: The system does not contain an object with ID: " + trg);
			
			Container srcContainer = (Container) view.getObjectState(src);
			Container trgContainer = (Container) view.getObjectState(trg);
			
			if (this.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + this.id + " has been deleted"); 
			
			if (srcContainer.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + srcContainer.getId() + " has been deleted");
			
			if (trgContainer.isDeleted())
				return new TransactionResult(false, "ERROR: The object " + trgContainer.getId() + " has been deleted");
			
			if (this.container != src)
				return new TransactionResult(false, "ERROR: The item " + this.getId() + " is not in the container " + src);
			
	//		if (!srcContainer.isOnDoc())
	//			return new TransactionResult(false,  "ERROR: " + src + " is not on dock");
	//		
	//		if (!trgContainer.isOnDoc())
	//			return new TransactionResult(false,  "ERROR: " + trg + " is not on dock");
			
			if (srcContainer.getDoc() != trgContainer.getDoc())
				return new TransactionResult(false,  "ERROR: " + src + " and " + trg + " are not on the same dock");
			
			return new TransactionResult(true, "OK");
		}
	}
	
	public void move(String src, String trg, SupplyChainView view) {
		synchronized (view) {
			Container srcContainer = ((Container) view.getObjectState(src)).deepCopy();
			Container trgContainer = ((Container) view.getObjectState(trg)).deepCopy();
			Item itemNextState = this.deepCopy(); 
			
			itemNextState.setContainer(trgContainer.getId());
			srcContainer.removeItem(getId());
			trgContainer.addItem(getId());
			
			view.addNextState(itemNextState);
			view.addNextState(srcContainer);
			view.addNextState(trgContainer);
		}
	}
}	
