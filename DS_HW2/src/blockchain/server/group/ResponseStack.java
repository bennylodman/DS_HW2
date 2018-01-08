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
	
	public synchronized void reset(int relevantBlockDepth, MessageType type) {
		this.stack = new ArrayList<>();
		this.relevantMessageDepth = relevantBlockDepth;
		this.type = type;
	}
	
	public synchronized List<SupplyChainMessage> fetchStack() {
		List<SupplyChainMessage> currentStack = new ArrayList<>();
		for (SupplyChainMessage scMessage : stack) {
			currentStack.add(scMessage.deepCopy());
		}
		return currentStack;
	}
	
	public synchronized void addIfRelevant(SupplyChainMessage msg) {
		System.out.println("@@@ msg.getType(): " + msg.getType());
		System.out.println("@@@ type: " + type);
		if (type != msg.getType())
			return;
		System.out.println("@#@#@#@#@#@#@#");
		System.out.println("Integer.parseInt(msg.getArgs()): " + Integer.parseInt(msg.getArgs()));
		System.out.println("relevantMessageDepth: " + relevantMessageDepth);
		if (Integer.parseInt(msg.getArgs()) == relevantMessageDepth) {
			stack.add(msg);
		}
	}
	
}	
