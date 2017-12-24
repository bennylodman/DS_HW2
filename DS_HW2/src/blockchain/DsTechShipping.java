package blockchain;


/**
 * 
 * this is the main class, will handle the zookeeper handler, the GroupServers communication and the REST server
 *
 */
public class DsTechShipping {
	
	private ZooKeeperHandler zkh;
	private GroupServers gs;
	private BlockChainView view;
	
	public ZooKeeperHandler getZooKeeperHandler() {
		return zkh;
	}
	public void setZooKeeperHandler(ZooKeeperHandler zkh) {
		this.zkh = zkh;
	}
	public GroupServers getGroupServers() {
		return gs;
	}
	public void setGroupServers(GroupServers gs) {
		this.gs = gs;
	}
	public BlockChainView getBlockChainView() {
		return view;
	}
	public void setBlockChainView(BlockChainView view) {
		this.view = view;
	}
	
}
