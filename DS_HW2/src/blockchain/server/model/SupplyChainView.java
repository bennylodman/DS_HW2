package blockchain.server.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import blockchain.server.model.SupplyChainObject;
import utils.ReadersWritersLock;

public class SupplyChainView {
	public Map<String, List<SupplyChainObject>> getSystemObjects() {
		return systemObjects;
	}

	private Map<String, List<SupplyChainObject>> systemObjects;
	private String knownBlocksPath;
	private int knownBlocksDepth;
	private List<Block> blockChain;
	private ReadersWritersLock rwl;
	
	public SupplyChainView() {
		this(0, "/");
	}
	
	public SupplyChainView(int knownBlocksDepth, String knownBlocksPath) {
		systemObjects = new HashMap<>();
		rwl = new ReadersWritersLock();
		this.knownBlocksPath = knownBlocksPath;
		this.knownBlocksDepth = knownBlocksDepth;
		blockChain = new ArrayList<>();
	}
	
	public ReadersWritersLock getRWLock() {
		return rwl;
	}

	public void setRWLock(ReadersWritersLock rwl) {
		this.rwl = rwl;
	}
	
	public String getKnownBlocksPath() {
		synchronized (blockChain) {
			return knownBlocksPath;
		}
	}
 
	public int getKnownBlocksDepth() {
		synchronized (blockChain) {
			return knownBlocksDepth;
		}
	}

	public void addToBlockChain(Block block) {
		synchronized (blockChain) {
			if (block.getDepth() != this.knownBlocksDepth + 1)
				return;
			
			blockChain.add(block);
			knownBlocksPath = knownBlocksPath + "/" + block.getBlockName();
			this.knownBlocksDepth++;
		}
	}
	
	
	public Block getFromBlockChain(int depth) {
		synchronized (blockChain) {
			if (blockChain.size() < depth) 
				return null;
			
			return blockChain.get(depth - 1);
		}
	}
	
	public void createObject(SupplyChainObject scObject) {
		if (systemObjects.containsKey(scObject.getId())) {
			return;
		}
		
		List<SupplyChainObject> history = new LinkedList<SupplyChainObject>();
		history.add(scObject);
		systemObjects.put(scObject.getId(), history);
	}
	
	public boolean hasObject(String id) {
		return systemObjects.containsKey(id);
	}
	
	public SupplyChainObject getObjectState(String id) {
		if (!systemObjects.containsKey(id))
			return null;
			
		List<SupplyChainObject> history = systemObjects.get(id);
		return history.get(history.size() - 1);
	}
	
	public List<SupplyChainObject> getObjectHistory(String id) {
		if (!systemObjects.containsKey(id))
			return null;
			
		return systemObjects.get(id);
	}
	
	public void addNextState(SupplyChainObject obj) {
		if (!systemObjects.containsKey(obj.getId()))
			return;
		
		systemObjects.get(obj.getId()).add(obj);
	}
	
	public SupplyChainView getCurrentView() {
		final SupplyChainView currentView = new SupplyChainView(this.getKnownBlocksDepth(), this.getKnownBlocksPath());
		
		this.systemObjects.forEach((id, hist) -> {
			SupplyChainObject obj = hist.get(hist.size() - 1);
			currentView.createObject(obj.deepCopy());
		});
		
		return currentView;
	}
}
