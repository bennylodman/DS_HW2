package blockchain.server.group;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import blockchain.server.model.SupplyChainMessage;

public class GroupServers extends ReceiverAdapter {
	private JChannel channel;
	private String serverName = System.getProperty("user.name", "n/a");
	
	
	public GroupServers() throws Exception {
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

	public void receive(Message msg) {
		SupplyChainMessage scMessage = msg.getObject();
		//update view according to block.
		
	}
	
	public void send(SupplyChainMessage scMessage) {
		try {
			this.channel.send(new Message(null, scMessage));
		} catch (Exception e) {
			System.out.println("ERROR: failed to send message");
		}
	}

	public
}
