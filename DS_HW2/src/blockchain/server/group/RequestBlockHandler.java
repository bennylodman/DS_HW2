package blockchain.server.group;

import org.jgroups.JChannel;
import org.jgroups.Message;

import blockchain.server.model.Block;
import blockchain.server.model.SupplyChainMessage;
import blockchain.server.model.SupplyChainView;

public class RequestBlockHandler extends Thread {
	private SupplyChainView view;
	private SupplyChainMessage message;
	private JChannel channel;
	private String serverName;
	
	public RequestBlockHandler(SupplyChainView view, SupplyChainMessage message, JChannel channel, String serverName) {
		this.view = view;
		this.message = message;
		this.channel = channel;
		this.serverName = serverName;
	}
	
    public void run() {
    	Block block = view.getFromBlockChain(message.getBlock().getDepth());
		SupplyChainMessage resopnse = new SupplyChainMessage(MessageType.ACK);
		resopnse.setTargetName(message.getSendersName());
		resopnse.setSendersName(serverName);
		
    	if (block == null) {
    		resopnse.setBlock(null);
    	} else {
    		resopnse.setBlock(block);
    	}
    	
    	try {
			synchronized (channel) {
				channel.send(new Message(null, resopnse));
			}
		} catch (Exception e) {
			System.out.println("RequestBlockHandler: failed to send message. error: " + e.getMessage());
		}
    }
}
