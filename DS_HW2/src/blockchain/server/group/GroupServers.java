package blockchain.server.group;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import blockchain.server.model.SupplyChainMessage;
import blockchain.server.model.SupplyChainView;

public class GroupServers extends ReceiverAdapter {
	private static int RESPONSE_TIMEOUT = 1;
	
	private JChannel channel;
	private String serverName = System.getProperty("user.name", "n/a");
	private SupplyChainView view;
	private ResponseStack rStack;
	
	public GroupServers(SupplyChainView view) throws Exception {
		this.view = view;
		channel = new JChannel("config/tcp.xml");
		channel.setReceiver(this);
		channel.connect("GroupServers");
	}
	
	public JChannel getChannel() {
		return channel;
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public String getServerName() {
		return serverName;
	}
	
	public void requestBlock(int blockDepth) {
		rStack.reset(blockDepth);
		SupplyChainMessage scMessage = new SupplyChainMessage(MessageType.REQUEST_BLOCK);
		scMessage.setArgs(String.valueOf(blockDepth));
		try {
			this.channel.send(new Message(null, scMessage));
		} catch (Exception e) {
			System.out.println("requestBlock: failed to send message");
		}
	}
	
	public void publishBlock(SupplyChainMessage msg) {
		rStack.reset(msg.getBlock().getDepth());
		try {
			this.channel.send(new Message(null, msg));
		} catch (Exception e) {
			System.out.println("publishBlock: failed to send message");
		}
	}
	
	public List<SupplyChainMessage> waitForResponse() {
		try {
			TimeUnit.SECONDS.sleep(RESPONSE_TIMEOUT);
		} catch (InterruptedException e) {}
		return rStack.fetchStack();
	}

	public void receive(Message msg) {
		SupplyChainMessage scMessage = msg.getObject();
		//TODO: check if this message is for me
		
		switch (scMessage.getType()) {
			case PUBLISHE_BLOCK: {
				new UpdateViewHandler(view, scMessage).start();
				break;
			}
			
			case REQUEST_BLOCK: {
				new RequestBlockHandler(view, scMessage, channel, serverName).start();
				break;
			}
			
			case ACK: {
				break;
			}
			
			case RESPONSE_BLOCK: {
				
				break;
			}
		}
	}
	
//	public void send(SupplyChainMessage scMessage) {
//		try {
//			this.channel.send(new Message(null, scMessage));
//		} catch (Exception e) {
//			System.out.println("ERROR: failed to send message");
//		}
//	}

}
