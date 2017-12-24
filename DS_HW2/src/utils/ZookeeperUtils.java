package utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import com.google.gson.Gson;

import blockchain.Block;

public class ZookeeperUtils {
	public static String TEMP_NODE_PREFIX = "__";
	public static Gson gson = new Gson();
	
	
	/**
	 * create the path to node at given depth
	 * 
	 * @param depthStr - the depth as string.
	 * 
	 * @return return the path to the node at /1/2/.../int(depthStr)
	 */
	public static String createPath(String depthStr) {
		String path = "/";
		int depth = Integer.parseInt(depthStr);
		for (int i = 1; i < depth; i++) {
			path += String.valueOf(i) + "/";
		}
		return path + String.valueOf(depth);
	}
	
	
	/**
	 * check if the znode which specified at 'path' hase child which is a Block (not temporal node)
	 * 
	 * @param path - path to the znode which we want to check
	 * 
	 */
	public static boolean hasNextBlock(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
		List<String> childrenList = zk.getChildren(path, false);
		for (String nodeName : childrenList) {
			if (!nodeName.startsWith(TEMP_NODE_PREFIX))
				return true;
		}
		return false;
	}
	
	
	/**
	 * created persistent znode after the node which specified at 'path' 
	 * 
	 * @param path - path to the znode which we want to add block after it.
	 * @param data - json string of Block class.
	 * 
	 * @return the actual path of the created node
	 */
	public static String createBlock(ZooKeeper zk, String path, String data) throws KeeperException, InterruptedException {
		byte[] dataAsBytes = data.getBytes(StandardCharsets.UTF_8);
		List<ACL> acl = Arrays.asList(new ACL[]{new ACL(Perms.ALL, Ids.ANYONE_ID_UNSAFE)});
		return zk.create(path, dataAsBytes, acl, CreateMode.PERSISTENT);
	}
	
	
	/**
	 * created persistent znode after the node which specified at 'path' 
	 * 
	 * @param path - path to the znode which we want to add block after it.
	 * @param data - Block object which we want to set as data of the new node.
	 * 
	 * @return the actual path of the created node
	 */
	public static String createBlock(ZooKeeper zk, String path, Block data) throws KeeperException, InterruptedException {
		String dataAsString = gson.toJson(data);
		return createBlock(zk, path, dataAsString);
	}
	
	
	/**
	 * created persistent znode after the node which specified at 'path', the new node name will with TEMP_NODE_PREFIX
	 * and then continued with generated sequential number.
	 * 
	 * @param path - path to the znode which we want to add temp node after it.
	 * 
	 * @return the actual path of the created node
	 */
	public static String createTemporalNode(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
		List<ACL> acl = Arrays.asList(new ACL[]{new ACL(Perms.ALL, Ids.ANYONE_ID_UNSAFE)});
		return zk.create(path, new byte[0], acl, CreateMode.PERSISTENT_SEQUENTIAL);
	}
	
	public static List<String> getAllChildrens(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
		return zk.getChildren(path, false);
	}
	
	public static int getNodeVersion(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
		Stat stat = new Stat();
		zk.getData(path, false, stat);
		return stat.getAversion();
	}
	
	
	/**
	 * @param path - path to the znode which we want to delete
	 * @param version - the version of the znode (can achieved by calling to getNodeVersion(..))
	 */
	public static void deleteNode(ZooKeeper zk, String path, int version) throws InterruptedException, KeeperException {
		zk.delete(path, version);
	}
	
	
	/**
	 * @param path - path to the znode which we want to read it's data
	 * @param cls -  class of the object type of the znode data. (Block\Ship\Container\Item).class
	 * 
	 * @return the data as object from the given class
	 */
	public static Object getNodeData(ZooKeeper zk, String path, Class<?> cls) throws KeeperException, InterruptedException { 
		Stat stat = new Stat();
		byte[] dataAsBytes = zk.getData(path, false, stat);
		String dataAsString = new String(dataAsBytes, StandardCharsets.UTF_8);
		return gson.fromJson(dataAsString, cls);
	} 

	
	public static void waitForNodeDeletion(ZooKeeper zk, String path, Object mutex) throws KeeperException, InterruptedException { //this function return only when the node was deleted.
		synchronized (mutex) {
			Stat stat = zk.exists(path, true);
	        if (stat != null) {
	            mutex.wait();
	        } 
		}
	} 
}
