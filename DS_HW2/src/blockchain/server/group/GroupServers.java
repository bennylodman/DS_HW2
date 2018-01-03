package blockchain.server.group;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import com.google.gson.Gson;

import blockchain.server.model.Block;

public class GroupServers extends ReceiverAdapter {
	private static Gson gson = new Gson();
	
	private JChannel channel;
	private String userName = System.getProperty("user.name", "n/a");
	
	
	public GroupServers() {
		try {
			channel = new JChannel("config/tcp.xml");
			channel.setReceiver(this);
			channel.connect("GroupServers");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JChannel getChannel() {
		return channel;
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String user) {
		this.userName = user;
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
}
