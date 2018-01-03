package blockchain.server.group;


public class TransactionResult {
	private boolean status;
	private String message;
	
	public TransactionResult() {
		this.status = false;
		this.message = "";
	}
	
	public TransactionResult(boolean status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public boolean getStatus() {
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
}
