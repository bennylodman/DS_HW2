package blockchain.server.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import blockchain.server.model.SupplyChainObject;
import utils.ReadersWritersLock;

public class SupplyChainView {
	private Map<String, List<SupplyChainObject>> systemObjects;
	private ReadersWritersLock rwl;
	
	public SupplyChainView() {
		this.systemObjects = new HashMap<>();
		this.rwl = new ReadersWritersLock();
	}
	
	public ReadersWritersLock getRWLock() {
		return rwl;
	}

	public void setRWLock(ReadersWritersLock rwl) {
		this.rwl = rwl;
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
		final SupplyChainView currentView = new SupplyChainView();
		
		this.systemObjects.forEach((id, hist) -> {
			SupplyChainObject obj = hist.get(hist.size() - 1);
			currentView.createObject(obj.deepCopy());
		});
		
		return currentView;
	}
}
