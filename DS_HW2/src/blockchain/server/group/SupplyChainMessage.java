package blockchain.server.group;

import java.util.ArrayList;
import java.util.List;

public class SupplyChainMessage {
	private String blockName;
	private Integer depth;
	private MessageType type;
	private List<Transaction> transactions;
	
	public SupplyChainMessage(MessageType type) {
		this(null, null, type);
	}
	
	public SupplyChainMessage(String blockName, Integer depth, MessageType type) {
		this.blockName = blockName;
		this.depth = depth;
		this.type = type;
		this.transactions = new ArrayList<>();
	}
	
	public String getBlockName() {
		return blockName;
	}
	
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
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
	
}
