package blockchain.server.model;

import java.util.ArrayList;
import java.util.List;

import blockchain.server.group.MessageType;

public class SupplyChainMessage {
	private String blockName;
	private String sendersName;
	private String targetName;
	private int depth;
	private MessageType type;
	private List<Transaction> transactions;
	
	public SupplyChainMessage(MessageType type) {
		this(null, type);
	}
	
	public SupplyChainMessage(String blockName, MessageType type) {
		this.blockName = blockName;
		this.type = type;
		this.transactions = new ArrayList<>();
	}
	
	public String getBlockName() {
		return blockName;
	}
	
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
	}
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public void addTransaction(Transaction trans) {
		this.transactions.add(trans);
	}
	
	public void removeTransactione(int index) {
		this.transactions.remove(index);
	}
	
	public String getSendersName() {
		return sendersName;
	}

	public void setSendersName(String sendersName) {
		this.sendersName = sendersName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void applyTransactions(SupplyChainView view) {
		view.getRWLock().acquireWrite();
		for (Transaction trans : getTransactions()) {
			switch (trans.getOperationType()) {
				case MOVE: {
					SupplyChainObject obj = view.getObjectState(trans.getObjectId());
					obj.move(trans.getSource(), trans.getTarget(), view);
					break;
				}
				
				case CREATE: {
					if (trans.getArgs()[0].equals(SupplyChainObject.ITEM)) {
						Item.create(trans.getObjectId(), trans.getTarget(), view);
					} else if (trans.getArgs()[0].equals(SupplyChainObject.CONTAINER)) {
						Container.create(trans.getObjectId(), trans.getTarget(), view);
					} else if (trans.getArgs()[0].equals(SupplyChainObject.SHIP)) {
						Ship.create(trans.getObjectId(), trans.getTarget(), view);
					}
					break;
				}
				
				case DELETE: {
					SupplyChainObject obj = view.getObjectState(trans.getObjectId());
					obj.delete(view);
					break;
				}
			}
		}
		view.getRWLock().releaseWrite();
	}
}
