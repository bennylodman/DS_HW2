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
	
	public synchronized List<SupplyChainMessage> fetchStack() {
		List<SupplyChainMessage> currentStack = stack;
		stack = new ArrayList<>();
		return currentStack;
	}
	
	public synchronized void addIfRelevant(SupplyChainMessage msg) {
		if (type != msg.getType())
			return;
		
		if (Integer.parseInt(msg.getArgs()) == relevantMessageDepth) {
			stack.add(msg);
		}
	}
	
}	
