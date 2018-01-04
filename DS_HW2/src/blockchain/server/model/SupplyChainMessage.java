package blockchain.server.model;

import java.util.ArrayList;
import java.util.List;

import blockchain.server.group.MessageType;

public class SupplyChainMessage {
	private String sendersName;
	private String targetName;
	private MessageType type;
	private Block block;
	
	public SupplyChainMessage(MessageType type) {
		this(null, type);
	}
	
	public SupplyChainMessage(String blockName, MessageType type) {
		this.type = type;
		this.block = new Block(blockName, null);
	}
	
	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public MessageType getType() {
		return type;
	}
	
	public void setType(MessageType type) {
		this.type = type;
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
}
