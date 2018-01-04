package blockchain.server.group;

import java.util.ArrayList;
import java.util.List;

import blockchain.server.model.SupplyChainMessage;

public class ResponseStack {
	private List<SupplyChainMessage> stack;
	private Integer relevantMessageDepth;
	private MessageType type;
	
	public ResponseStack() {
		this.stack = new ArrayList<>();
		this.relevantMessageDepth = null;
	}
	
	public synchronized void reset(int relevantBlockDepth) {
		this.stack = new ArrayList<>();
		this.relevantMessageDepth = relevantBlockDepth;
	}
	
	public synchronized void addResponse(SupplyChainMessage msg) {
		stack.add(msg);
	}
	
	public synchronized List<SupplyChainMessage> fetchStack() {
		List<SupplyChainMessage> currentStack = stack;
		stack = new ArrayList<>();
		return currentStack;
	}
	
	public synchronized boolean isRelevant(SupplyChainMessage msg) {
		if (type != msg.getType())
			return false;
		
		if (type == MessageType.ACK) {
			return Integer.parseInt(msg.getArgs()) == relevantMessageDepth;
		}
		
		if (type == MessageType.RESPONSE_BLOCK) {
			return msg.getBlock().getDepth() == relevantMessageDepth;
		}
		
		return false;
	}
	
}	
