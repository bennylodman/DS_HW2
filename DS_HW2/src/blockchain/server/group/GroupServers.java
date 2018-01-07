package blockchain.server.group;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import blockchain.server.DsTechShipping;
import blockchain.server.model.Container;
import blockchain.server.model.SupplyChainMessage;
import blockchain.server.model.SupplyChainView;

public class GroupServers extends ReceiverAdapter {
	private static int RESPONSE_TIMEOUT = 1;
	private static String BRODSCST = "ALL"; 
	
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
		scMessage.setTargetName(BRODSCST);
		scMessage.setSendersName(serverName);
		try {
			this.channel.send(new Message(null, scMessage));
		} catch (Exception e) {
			System.out.println("requestBlock: failed to send message");
		}
	}
	
	public void publishBlock(SupplyChainMessage msg) {
		rStack.reset(msg.getBlock().getDepth());
		msg.setTargetName(BRODSCST);
		msg.setSendersName(serverName);
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
		
		switch (scMessage.getType()) {
			case PUBLISHE_BLOCK: {
				if (scMessage.getSendersName() != serverName)
					new UpdateViewHandler(view, scMessage, channel, serverName, DsTechShipping.zkHandler).start();
				break;
			}
			
			case REQUEST_BLOCK: {
				if (scMessage.getTargetName() == serverName || scMessage.getTargetName() == BRODSCST)
					new RequestBlockHandler(view, scMessage, channel, serverName).start();
				break;
			}
			
			case ACK: {
				rStack.addIfRelevant(scMessage);
				break;
			}
			
			case RESPONSE_BLOCK: {
				rStack.addIfRelevant(scMessage);
				break;
			}
		}
	}
}
