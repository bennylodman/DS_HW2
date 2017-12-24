package blockchain;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

import com.google.gson.Gson;

public class GroupServers extends ReceiverAdapter {
	JChannel channel;
	Object view;
	Gson gson = new Gson();
	String user_name = System.getProperty("user.name", "n/a");
	
	public JChannel getChannel() {
		return channel;
	}

	public void setChannel(JChannel channel) {
		this.channel = channel;
	}

	public Object getView() {
		return view;
	}

	public void setView(Object view) {
		this.view = view;
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public GroupServers(Object view) {
		try {
			this.view = view;
			channel = new JChannel("config/tcp.xml");
			channel.setReceiver(this);
			channel.connect("GroupServers");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receive(Message msg) {
		 String blockStr = msg.getObject();
		 Block block = this.gson.fromJson(blockStr, Block.class);
		//update view according to block.
		
	}
	
	public void send(String msg) {
		try {
			this.channel.send(new Message(null, msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
