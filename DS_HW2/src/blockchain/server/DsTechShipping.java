package blockchain.server;

import blockchain.server.group.BlockHandler;
import blockchain.server.group.GroupServers;
import blockchain.server.group.Operation;
import blockchain.server.group.Transaction;
import blockchain.server.group.TransactionResult;
import blockchain.server.model.SupplyChainView;
import blockchain.server.zoo.ZooKeeperHandler;


public class DsTechShipping {
	
	private ZooKeeperHandler zkHandler;
	private GroupServers groupServers;
	private SupplyChainView view;
	private BlockHandler[] blocksHandlers; // always contains 2 blocks and exactly one block is open at any time.
	
	public DsTechShipping() {
		//TODO: initialize zookeeper and jGroup
		
		this.blocksHandlers = new BlockHandler[]{new BlockHandler(true), new BlockHandler(false)};
	}
	
	public ZooKeeperHandler getZooKeeperHandler() {
		return zkHandler;
	}
	
	public void setZooKeeperHandler(ZooKeeperHandler zkh) {
		this.zkHandler = zkh;
	}
	
	public GroupServers getGroupServers() {
		return groupServers;
	}
	
	public void setGroupServers(GroupServers gs) {
		this.groupServers = gs;
	}
	
	public SupplyChainView getBlockChainView() {
		return view;
	}
	
	public void setBlockChainView(SupplyChainView view) {
		this.view = view;
	}
	
	public BlockHandler[] getBlocksHandler() {
		return blocksHandlers;
	}

	public void setBlocksHandler(BlockHandler[] blocks) {
		this.blocksHandlers = blocks;
	}
	
	public TransactionResult addTransaction(Transaction trans) throws InterruptedException {
		synchronized (blocksHandlers) {
			TransactionResult res = null;
			for (int i = 0; i < blocksHandlers.length; i++) {
				if (blocksHandlers[i].isOpen()) {
					res = blocksHandlers[i].addTransaction(trans);
				}
			}
			return res;
		}
	}
	
	//#############################################################
	// REST back-end
	//#############################################################
	
	public TransactionResult createShip(String id, String docId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, new String[]{docId}));
	}
	
	public TransactionResult createContainer(String id, String shipId, String docId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, new String[]{shipId, docId}));
	}
	
	public TransactionResult createItem(String id, String containerId, String shipId, String docId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, new String[]{containerId, shipId, docId}));
	}
	
	public TransactionResult deleteSupplyChainObject(String id) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.DELETE));
	}
	
	public TransactionResult moveSupplyChainObject(String id, String src, String dst) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.MOVE, src, dst));
	}
	
	void getShipState() {}
	void getContainerState() {}
	void getItemState() {}
	void getDocState() {}
	
	void getHistory() {}
}
