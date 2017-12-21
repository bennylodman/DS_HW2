package utils;

import java.util.List;

public class ZookeeperUtils {
	String createPath(String depth) {}
	
	boolean hasNextBlock(String path) {}
	
	String createBlock(String path, String data) {}
	
	String createTemporalNode(String path) {}
	
	List<String> getAllChildrens(String path) {}
	
	String deleteNode(String path) {}
	
	Object getNodeData(String path, Class<?> cls) {} //(Block\ship\container).class
	
	Block getBlockData(String path) {} // only from blockchain
	
	void waitForNodeDeletion(...) {} //this function return only when the node was deleted.
	
	
}
