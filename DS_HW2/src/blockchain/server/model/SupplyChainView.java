package blockchain.server.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import blockchain.server.model.Container;
import blockchain.server.model.SupplyChainObject;

public class SupplyChainView {
	private Map<String, List<SupplyChainObject>> systemObjects;
	
	public SupplyChainView() {
		this.systemObjects = new HashMap<>();
	}
	
	public void createObject(SupplyChainObject scObject) {
		if (systemObjects.containsKey(scObject.getId())) 
			return;
		
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
	
//	public SupplyChainObject createNextState(String id) { //create next node by doing deep copy of the current last state
//		List<SupplyChainObject> history = localObjects.get(id);
//		SupplyChainObject currentState =  history.get(history.size() - 1);
//		SupplyChainObject newState = deepCopy(currentState, SupplyChainObject.class);
//		history.add(newState);
//		return newState;
//	}
	

}
