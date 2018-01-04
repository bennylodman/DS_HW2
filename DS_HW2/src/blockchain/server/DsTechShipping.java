package blockchain.server;

import blockchain.server.group.BlockHandler;
import blockchain.server.group.GroupServers;
import blockchain.server.group.Operation;
import blockchain.server.model.SupplyChainObject;
import blockchain.server.model.SupplyChainView;
import blockchain.server.model.Transaction;
import blockchain.server.model.TransactionResult;
import blockchain.server.zoo.ZooKeeperHandler;


public class DsTechShipping {

	public static Integer MaxServersCrushSupport = 1;
	public static ZooKeeperHandler zkHandler;
	public static GroupServers groupServers;
	public static SupplyChainView view;
	public static BlockHandler blocksHandler; // always contains 2 blocks and exactly one block is open at any time.
	public static Object blockHandlerLock;

	public DsTechShipping() {
		//TODO: initialize zookeeper and jGroup
		
		this.blocksHandler = new BlockHandler();
		this.blockHandlerLock = new Object();
	}
	
	public static ZooKeeperHandler getZooKeeperHandler() {
		return zkHandler;
	}
	
	public static void setZooKeeperHandler(ZooKeeperHandler zkh) {
		this.zkHandler = zkh;
	}
	
	public static GroupServers getGroupServers() {
		return groupServers;
	}
	
	public static void setGroupServers(GroupServers gs) {
		this.groupServers = gs;
	}
	
	public static SupplyChainView getBlockChainView() {
		return view;
	}
	
	public static void setBlockChainView(SupplyChainView view) {
		this.view = view;
	}
	
	public static BlockHandler getBlocksHandler() {
		return blocksHandler;
	}

	public static void setBlockHandler(BlockHandler block) {
		this.blocksHandler = block;
	}
	
	public static TransactionResult addTransaction(Transaction trans) throws InterruptedException {
		synchronized (blockHandlerLock) {
			return blocksHandler.addTransaction(trans);
		}
	}
	
	//#############################################################
	// REST back-end
	//#############################################################
	
	public static TransactionResult createShip(String id, String docId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, docId, new String[]{SupplyChainObject.SHIP}));
	}
	
	public static TransactionResult createContainer(String id, String shipId, String docId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, shipId, new String[]{SupplyChainObject.CONTAINER}));
	}
	
	public static TransactionResult createItem(String id, String containerId, String shipId, String docId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, containerId, new String[]{SupplyChainObject.ITEM}));
	}
	
	public  static TransactionResult deleteSupplyChainObject(String id) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.DELETE));
	}
	
	public static TransactionResult moveSupplyChainObject(String id, String src, String trg) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.MOVE, src, trg));
	}
	
	void getShipState() {}
	void getContainerState() {}
	void getItemState() {}
	void getDocState() {}
	
	void getHistory() {}
}
