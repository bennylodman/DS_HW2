package blockchain;


public class Block {
	private String id;
	private String op;
	private String arg;
	
	public Block(String id, String op) {
		this(id, op, null);
	}
	
	public Block(String id, String op, String arg) {
		this.id = id;
		this.op = op;
		this.arg = arg;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOp() {
		return op;
	}
	
	public void setOp(String op) {
		this.op = op;
	}
	
	public String getArg() {
		return arg;
	}
	
	public void setArg(String arg) {
		this.arg = arg;
	}
	
}
