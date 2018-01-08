package blockchain.server;


import java.util.List;
import java.util.concurrent.TimeUnit;

import blockchain.server.group.BlockHandler;
import blockchain.server.group.GroupServers;
import blockchain.server.group.Operation;
import blockchain.server.model.Container;
import blockchain.server.model.Item;
import blockchain.server.model.QueryResult;
import blockchain.server.model.Ship;
import blockchain.server.model.SupplyChainObject;
import blockchain.server.model.SupplyChainView;
import blockchain.server.model.Transaction;
import blockchain.server.model.TransactionResult;
import blockchain.server.zoo.ZooKeeperHandler;


public class DsTechShipping {

	public static Integer MaxServersCrushSupport = 1;
	public static ZooKeeperHandler zkHandler = new ZooKeeperHandler();
	public static GroupServers groupServers;
	public static SupplyChainView view = new SupplyChainView();
	public static BlockHandler blocksHandler = new BlockHandler(); // always contains 2 blocks and exactly one block is open at any time.
	public static Object blockHandlerLock = new Object();

	public static void initialize() {
		DsTechShipping.groupServers = new GroupServers(view);
		new ServerThread().start();
	}
	
	public static ZooKeeperHandler getZooKeeperHandler() {
		return zkHandler;
	}
	
	public static void setZooKeeperHandler(ZooKeeperHandler zkh) {
		DsTechShipping.zkHandler = zkh;
	}
	
	public static GroupServers getGroupServers() {
		return groupServers;
	}
	
	public static void setGroupServers(GroupServers gs) {
		DsTechShipping.groupServers = gs;
	}
	
	public static SupplyChainView getBlockChainView() {
		return view;
	}
	
	public static void setBlockChainView(SupplyChainView view) {
		DsTechShipping.view = view;
	}
	
	public static BlockHandler getBlocksHandler() {
		return blocksHandler;
	}

	public static void setBlockHandler(BlockHandler block) {
		DsTechShipping.blocksHandler = block;
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
	
	public static TransactionResult createContainer(String id, String shipId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, shipId, new String[]{SupplyChainObject.CONTAINER}));
	}
	
	public static TransactionResult createItem(String id, String containerId) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.CREATE, containerId, new String[]{SupplyChainObject.ITEM}));
	}
	
	public  static TransactionResult deleteSupplyChainObject(String id) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.DELETE));
	}
	
	public static TransactionResult moveSupplyChainObject(String id, String src, String trg) throws InterruptedException {
		return addTransaction(new Transaction(id, Operation.MOVE, src, trg));
	}
	
	public static QueryResult getShipState(String id) {
		view.getRWLock().acquireRead();
		
		if (id == null || !id.startsWith(Ship.PREFIX))
			return new QueryResult(false, "ERROR: " + id + " is invalid ship ID");
		
		if (!view.hasObject(id))
			return new QueryResult(false, "ERROR: The system does not contain an object with ID: " + id);
		
		SupplyChainObject ship = view.getObjectState(id);
		
		view.getRWLock().releaseRead();
		return new QueryResult(true, "OK", ship);
	}
	
	public static QueryResult getShipHistory(String id) {
		view.getRWLock().acquireRead();
		
		if (id == null || !id.startsWith(Ship.PREFIX))
			return new QueryResult(false, "ERROR: " + id + " is invalid ship ID");
		
		if (!view.hasObject(id))
			return new QueryResult(false, "ERROR: The system does not contain an object with ID: " + id);
		
		List<SupplyChainObject> shipHist = view.getObjectHistory(id);
		
		view.getRWLock().releaseRead();
		return new QueryResult(true, "OK", shipHist);
	}
	
	static QueryResult getContainerState(String id) {
		view.getRWLock().acquireRead();
		
		if (id == null || !id.startsWith(Container.PREFIX))
			return new QueryResult(false, "ERROR: " + id + " is invalid container ID");
		
		if (!view.hasObject(id))
			return new QueryResult(false, "ERROR: The system does not contain an object with ID: " + id);
		
		SupplyChainObject container = view.getObjectState(id);
		
		view.getRWLock().releaseRead();
		return new QueryResult(true, "OK", container);
	}
	
	public static QueryResult getContainerHist(String id) {
		view.getRWLock().acquireRead();
		
		if (id == null || !id.startsWith(Container.PREFIX))
			return new QueryResult(false, "ERROR: " + id + " is invalid container ID");
		
		if (!view.hasObject(id))
			return new QueryResult(false, "ERROR: The system does not contain an object with ID: " + id);
		
		List<SupplyChainObject> containerHist = view.getObjectHistory(id);
		
		view.getRWLock().releaseRead();
		return new QueryResult(true, "OK", containerHist);
	}
	
	public static QueryResult getItemState(String id) {
		view.getRWLock().acquireRead();
		
		if (id == null || !id.startsWith(Item.PREFIX))
			return new QueryResult(false, "ERROR: " + id + " is invalid item ID");
		
		if (!view.hasObject(id))
			return new QueryResult(false, "ERROR: The system does not contain an object with ID: " + id);
		
		SupplyChainObject item = view.getObjectState(id);
		
		view.getRWLock().releaseRead();
		return new QueryResult(true, "OK", item);
	}
	
	public static QueryResult getItemHist(String id) {
		view.getRWLock().acquireRead();
		
		if (id == null || !id.startsWith(Item.PREFIX))
			return new QueryResult(false, "ERROR: " + id + " is invalid item ID");
		
		if (!view.hasObject(id))
			return new QueryResult(false, "ERROR: The system does not contain an object with ID: " + id);
		
		List<SupplyChainObject> itemHist = view.getObjectHistory(id);
		
		view.getRWLock().releaseRead();
		return new QueryResult(true, "OK", itemHist);
	}
	
//	public static QueryResult getDocState(String id) {} //TODO
	
	public static void main(String[] args) {
		DsTechShipping.initialize();
        
		try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        
        try {
        	TransactionResult tr = createShip("S_titanic", "Haifa");
        	System.out.println("Status:" + tr.getStatus());
        	System.out.println("Message:" + tr.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert(false);
        }
        
	}
}
