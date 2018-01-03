package blockchain.server.group;

import java.util.ArrayList;
import java.util.List;

import utils.Tuple;



public class BlockHandler {
	private SupplyChainMessage scMessage;
	private List<WaitingObject> waitingThreadObjects; 
	private boolean open;
	
	public BlockHandler(boolean open) {
		this.scMessage = new SupplyChainMessage(MessageType.PUBLISHE_BLOCK);
		this.open = open;
		this.waitingThreadObjects = new ArrayList<>();
	}
	
	public TransactionResult addTransaction(Transaction trans) throws InterruptedException {
		if (!open) 
			return null;
		
		scMessage.addTransaction(trans);
		WaitingObject waitingObj = new WaitingObject();
		waitingThreadObjects.add(waitingObj);
		waitingObj.getLock().wait();
		return waitingObj.getResult();
	}
	
	public void notifyTransaction(int transIndex, boolean resStatus, String resMessage) throws InterruptedException {
		scMessage.removeTransactione(transIndex);
		WaitingObject waitingObj = waitingThreadObjects.get(transIndex);
		waitingObj.setResult(resStatus, resMessage);
		waitingThreadObjects.remove(transIndex);
		waitingObj.getLock().notify();
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void close() {
		this.open = false;
	}
	
	public int size() {
		return this.scMessage.getTransactions().size();
	}
}


class WaitingObject {
	private Object lock;
	private TransactionResult result;
	
	public WaitingObject() {
		this.lock = new Object();
		this.result = new TransactionResult();
	}
	
	public Object getLock() {
		return lock;
	}
	
	public TransactionResult getResult() {
		return result;
	}
	
	public void setResult(boolean status, String message) {
		this.result.setStatus(status);
		this.result.setMessage(message);
	}
	
	public void setResultStatus(boolean status) {
		this.result.setStatus(status);
	}
	
	public void setResultMessage(String message) {
		this.result.setMessage(message);
	}

	public boolean getResultStatus() {
		return this.result.getStatus();
	}

	public String getResultMessage() {
		return this.result.getMessage();
	}
}
