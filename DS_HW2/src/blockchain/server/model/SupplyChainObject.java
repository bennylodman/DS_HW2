package blockchain.server.model;

import blockchain.server.group.TransactionResult;

public abstract class SupplyChainObject {
	protected String id;
	protected boolean deleted;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	abstract public SupplyChainObject deepCopy();
	abstract public void move(String src, String trg, SupplyChainView currentView);
	abstract public void delete(SupplyChainView currentView);
	abstract public TransactionResult verifyMove(String src, String trg, SupplyChainView currentView);
	abstract public TransactionResult verifyDelete(SupplyChainView currentView);
}
