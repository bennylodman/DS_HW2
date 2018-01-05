package blockchain.server.group;

import org.jgroups.JChannel;
import org.jgroups.Message;

import blockchain.server.model.SupplyChainMessage;
import blockchain.server.model.SupplyChainView;

public class UpdateViewHandler extends Thread {
	private SupplyChainView view;
	private SupplyChainMessage message;
	private JChannel channel;
	private String serverName;
	
	public UpdateViewHandler(SupplyChainView view, SupplyChainMessage message, JChannel channel, String serverName) {
		this.view = view;
		this.message = message;
		this.channel = channel;
		this.serverName = serverName;
	}
	
    public void run() {
    	
    	// send Ack
		SupplyChainMessage resopnse = new SupplyChainMessage(MessageType.ACK);
		resopnse.setTargetName(message.getSendersName());
		resopnse.setSendersName(serverName);
		
    	try {
			synchronized (channel) {
				channel.send(new Message(null, resopnse));
			}
		} catch (Exception e) {
			System.out.println("RequestBlockHandler: failed to send message. error: " + e.getMessage());
		}
    	
    	// update local view.
    	while (message.getBlock().getDepth() != view.getKnownBlocksDepth() + 1) {
			try {
				view.wait();
			} catch (InterruptedException e) {}
    	}
    	
    	message.getBlock().applyTransactions(view);
    	view.addToBlockChain(message.getBlock());
    	view.notifyAll();
    	
    }
}
